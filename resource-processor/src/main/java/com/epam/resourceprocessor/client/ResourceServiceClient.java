package com.epam.resourceprocessor.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class ResourceServiceClient {

    private final RestClient restClient;

    public ResourceServiceClient(@Value("${resource.service.url:http://localhost:8080/resources}") String resourceServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(resourceServiceUrl).build();
    }

    @Retryable(
        retryFor = { RestClientException.class },
        noRetryFor = { HttpClientErrorException.class },
        maxAttempts = 5,
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public byte[] getResource(int id) {
        return restClient.get().uri("/{id}", id).retrieve().body(byte[].class);
    }
}
