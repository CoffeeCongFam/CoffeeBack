package com.ucamp.coffee.common.exception;

import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ApiResponse<Object>> handleException(CommonException ex) {
        Class<?> origin = extractOriginClass(ex);
        log.error("API 예외 발생: {} from {}", ex.getMessage(), origin.getSimpleName());
        return ResponseMapper.failOf(ex, origin);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleException(RuntimeException ex) {
        Class<?> origin = extractOriginClass(ex);
        log.error("[RuntimeException 발생] origin: {}, message: {}", origin.getSimpleName(), ex.getMessage(), ex);
        return ResponseMapper.failOf(ApiStatus.INTERNAL_SERVER_ERROR.getHttpStatus(), origin);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        Class<?> origin = extractOriginClass(ex);
        log.error("[Exception 발생] origin: {}, message: {}", origin.getSimpleName(), ex.getMessage(), ex);
        return ResponseMapper.failOf(ApiStatus.INTERNAL_SERVER_ERROR.getHttpStatus(), origin);
    }

    private Class<?> extractOriginClass(Exception ex) {
        for (StackTraceElement element : ex.getStackTrace()) {
            try {
                if (element.getClassName().startsWith("com.ucamp.coffee")) {
                    return Class.forName(element.getClassName());
                }
            } catch (ClassNotFoundException ignored) { /* 아무런 처리하지 않았습니다. */}
        }
        return GlobalExceptionHandler.class;
    }
}