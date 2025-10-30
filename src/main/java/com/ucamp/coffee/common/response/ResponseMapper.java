package com.ucamp.coffee.common.response;

import com.ucamp.coffee.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ResponseMapper {
    public static ResponseEntity<ApiResponse<?>> successOf(Object data) {
        ApiResponse<?> response = ApiResponse.builder()
            .success(true)
            .data(data)
            .message("요청이 성공적으로 처리되었습니다.")
            .build();
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<ApiResponse<Object>> failOf(HttpStatus status, Class<?> origin) {
        log.error("[{}] 오류가 발생했습니다.", origin.getSimpleName());

        ApiResponse<Object> response = ApiResponse.builder()
            .success(false)
            .data(null)
            .message("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
            .build();
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<ApiResponse<Object>> failOf(CommonException e, Class<?> origin) {
        log.error("[{}] 오류가 발생했습니다. {}", origin.getSimpleName(), e.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
            .success(false)
            .data(null)
            .message(e.getMessage())
            .build();
        return ResponseEntity.status(e.getStatus().getHttpStatus()).body(response);
    }
}