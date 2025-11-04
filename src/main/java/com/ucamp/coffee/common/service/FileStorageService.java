package com.ucamp.coffee.common.service;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public String save(MultipartFile file) {
        // 넘어온 파일이 없다면
        if (file == null || file.isEmpty()) return null;

        try {
            // 파일 없으면 파일 생성
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String originalName = file.getOriginalFilename();                       // 원본 파일명
            String ext = originalName.substring(originalName.lastIndexOf(".")); // 확장자
            String filename = UUID.randomUUID() + ext;                              // 파일명

            Path targetPath = Paths.get(uploadDir).resolve(filename); // 저장 경로
            Files.copy(file.getInputStream(), targetPath);

            return "/uploads/" + filename;
        } catch (Exception e) {
            throw new CommonException(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
