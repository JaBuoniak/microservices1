package com.epam.resourceservice.exception;

public class InvalidCsvParameterException extends RuntimeException {
    public InvalidCsvParameterException(String message) {
        super(message);
    }
}
