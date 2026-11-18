package com.epam.storageservice.dto;

public record StorageResponse(
        Integer id,
        String storageType,
        String bucket,
        String path
) {}
