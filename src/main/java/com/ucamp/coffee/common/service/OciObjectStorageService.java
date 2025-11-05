package com.ucamp.coffee.common.service;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OciObjectStorageService {
    private final ObjectStorageClient objectStorageClient;

    @Value("${oci.objectstorage.namespace}")
    private String namespace;

    @Value("${oci.objectstorage.bucket-name}")
    private String bucketName;

    @Value("${oci.objectstorage.region}")
    private String region;

    public String uploadFile(MultipartFile file) {
        File tempFile = null;
        try {
            // MultipartFile을 임시 파일로 저장
            tempFile = File.createTempFile("oci-upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            // 객체 이름 생성
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID() + fileExtension;

            // PutObjectRequest 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucketName)
                .objectName(objectName)
                .contentLength(tempFile.length())
                .contentType(file.getContentType())
                .putObjectBody(new FileInputStream(tempFile))
                .build();

            objectStorageClient.putObject(putObjectRequest);

            return String.format(
                "https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s",
                region,
                namespace,
                bucketName,
                objectName
            );
        } catch (Exception e) {
            throw new CommonException(ApiStatus.INTERNAL_SERVER_ERROR);
        } finally {
            // 임시 파일 삭제
            if (tempFile != null) tempFile.delete();
        }
    }
}
