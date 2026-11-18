package com.epam.storageservice.exception;

public class InvalidCsvParameterException extends RuntimeException {
    public InvalidCsvParameterException(String message) {
        super(message);
    }
}
