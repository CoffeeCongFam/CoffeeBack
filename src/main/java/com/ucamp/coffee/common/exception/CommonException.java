package com.ucamp.coffee.common.exception;

import com.ucamp.coffee.common.response.ApiStatus;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
    private final ApiStatus status;

    public CommonException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public CommonException(ApiStatus status, String message) {
        super(status.getMessage() + " " + message);
        this.status = status;
    }
}