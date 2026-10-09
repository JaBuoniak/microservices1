package com.epam.songservice.exception;

public class InvalidSongIdException extends RuntimeException {
    public InvalidSongIdException(String message) {
        super(message);
    }
}
