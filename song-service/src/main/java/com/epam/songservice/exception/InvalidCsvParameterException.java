package com.epam.songservice.exception;

public class InvalidCsvParameterException extends RuntimeException {
    public InvalidCsvParameterException(String message) {
        super(message);
    }
}
