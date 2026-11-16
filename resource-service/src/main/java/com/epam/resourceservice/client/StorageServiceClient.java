package com.epam.resourceservice.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.epam.resourceservice.dto.StorageResponse;

@Component
public class StorageServiceClient {

    private final RestClient restClient;

    public StorageServiceClient(@Value("${storage.service.url:http://localhost:8087/storages}") String storageServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(storageServiceUrl).build();
    }


    public List<StorageResponse> getStorages() {
        return restClient.get().retrieve().body(new ParameterizedTypeReference<List<StorageResponse>>() {});
    }

    public StorageResponse getStorageByType(String storageType) {
        List<StorageResponse> storages = getStorages();
        if (storages == null) {
            throw new IllegalStateException("Storage list is null");
        }
        return storages.stream()
                .filter(s -> storageType.equalsIgnoreCase(s.storageType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Storage with type '" + storageType + "' not found"));
    }
}
