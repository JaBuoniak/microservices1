package com.epam.songservice.exception;

public class SongAlreadyExistsException extends RuntimeException {
    public SongAlreadyExistsException(String message) {
        super(message);
    }
}
