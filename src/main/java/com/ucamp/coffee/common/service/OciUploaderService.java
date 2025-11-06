package com.ucamp.coffee.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class OciUploaderService {
    private final OciObjectStorageService ociObjectStorageService;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public String uploadSafely(MultipartFile file) {
        if (file == null || file.isEmpty()) return "";

        try {
            // OCI 호출이 실패해도, 외부 트랜잭션에 영향 없음
            return ociObjectStorageService.uploadFile(file);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ""; // 이미지 업로드 실패 시 빈 문자열 리턴
        }
    }
}
