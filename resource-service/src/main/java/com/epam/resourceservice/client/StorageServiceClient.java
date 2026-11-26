package com.epam.resourceservice.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.epam.resourceservice.dto.StorageResponse;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
public class StorageServiceClient {
    private static final Logger log = LoggerFactory.getLogger(StorageServiceClient.class);
    private static final String STORAGE_SERVICE_CB = "storageService";
    private final RestClient restClient;

    public StorageServiceClient(
            @Value("${storage.service.url:http://localhost:8087/storages}") String storageServiceUrl,
            CircuitBreakerRegistry circuitBreakerRegistry,
            RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(storageServiceUrl).build();
        circuitBreakerRegistry.circuitBreaker(STORAGE_SERVICE_CB)
                .getEventPublisher()
                .onStateTransition(event ->
                        log.info("CircuitBreaker '{}' state transition: {}",
                                event.getCircuitBreakerName(), event.getStateTransition()));
    }


    public List<StorageResponse> getStorages() {
        return restClient.get().retrieve().body(new ParameterizedTypeReference<List<StorageResponse>>() {});
    }

    @CircuitBreaker(name = STORAGE_SERVICE_CB, fallbackMethod = "getStorageByTypeFallback")
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

    private StorageResponse getStorageByTypeFallback(String storageType, Throwable throwable) {
        log.warn("Storage service unavailable for type '{}'. Using fallback stub data. Reason: {}",
                storageType, throwable.getMessage());
        List<StorageResponse> stubStorages = List.of(
                new StorageResponse(1, "STAGING", "staging-bucket", "/files"),
                new StorageResponse(2, "PERMANENT", "permanent-bucket", "/files")
        );
        return stubStorages.stream()
                .filter(s -> storageType.equalsIgnoreCase(s.storageType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Fallback storage with type '" + storageType + "' not found"));
    }
}
