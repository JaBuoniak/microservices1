package com.epam.resourceprocessor.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ResourceServiceClient {

    private final RestClient restClient;

    public ResourceServiceClient(@Value("${resource.service.url:http://localhost:8080/resources}") String resourceServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(resourceServiceUrl).build();
    }

    public byte[] getResource(int id) {
        return restClient.get().uri("/{id}", id).retrieve().body(byte[].class);
    }
}
