package com.nikiko.timemanager.exception;

import lombok.Getter;

public class ApiException extends RuntimeException{
    @Getter
    private String errorCode;
    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
