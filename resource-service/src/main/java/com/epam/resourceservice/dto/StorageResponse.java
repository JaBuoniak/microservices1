package com.epam.resourceservice.dto;

/**
 * StorageResponse
 */
public record StorageResponse(Integer id, String storageType, String bucket, String path) {

}
