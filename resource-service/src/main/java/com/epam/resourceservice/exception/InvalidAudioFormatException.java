package com.epam.resourceservice.exception;

public class InvalidAudioFormatException extends RuntimeException {
    public InvalidAudioFormatException(String message) {
        super(message);
    }
}
