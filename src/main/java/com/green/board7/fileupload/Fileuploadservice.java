package com.green.board7.fileupload;

import com.green.board7.fileupload.model.FileEntity;
import com.green.board7.fileupload.model.FileLoadDto;
import com.green.board7.fileupload.model.FileuploadInsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

@Service
public class Fileuploadservice {
    private FileuploadMapper mapper;

    @Autowired
    public Fileuploadservice(FileuploadMapper mapper) {
        this.mapper = mapper;
    }

    @Value("D:/download/")
    private String fileDir;

    public Resource fileLoad(FileLoadDto dto) {
        FileEntity entity = mapper.selFileById(dto);
        try {
            File file = new File(fileDir + entity.getPath());
            Resource resource = new UrlResource(file.toURI());
            if(resource.exists()) {
                return resource;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void fileUpload( FileuploadInsDto dto, MultipartFile img) {
        System.out.println("fileDir : " + fileDir);

        //원래 파일 이름 추출
        String originFileName = img.getOriginalFilename();

        //파일 이름으로 사용할 uuid 생성
        String uuid = UUID.randomUUID().toString(); //랜덤한 영어 숫자로 이루어진 문자를 파일명으로 이용

        int dotIdx = originFileName.lastIndexOf(".");
        String ext = originFileName.substring(dotIdx);

        String savedFileName = uuid + ext;
        String savedFilePath = fileDir + savedFileName;

        File file = new File(savedFilePath); //파일이 없어도 일단 만들어짐?
        try {
            img.transferTo(file); // 경로를 가지고있는 객체(file)을 메모리에 파일로 만들어줌

            //경로저장도하기 필요해서 추가!
            //Builder 최초 만들때 setter 대신 원하는 객체만 값을 넣을수있도록 도와주는 친구
            FileEntity entity = FileEntity.builder()
                    .path(savedFileName)
                    .uploader(dto.getUploader())
                    .levelValue(dto.getLevelValue())
                    .build();
            mapper.insFile(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
