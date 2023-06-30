package com.nikiko.timemanager.exception;

public class InvalidDataException extends ApiException{
    public InvalidDataException(String message, String errorCode) {
        super(message, errorCode);
    }
}
