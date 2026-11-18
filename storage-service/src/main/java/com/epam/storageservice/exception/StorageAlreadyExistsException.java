package com.epam.storageservice.exception;

public class StorageAlreadyExistsException extends RuntimeException {
    public StorageAlreadyExistsException(String message) {
        super(message);
    }
}
