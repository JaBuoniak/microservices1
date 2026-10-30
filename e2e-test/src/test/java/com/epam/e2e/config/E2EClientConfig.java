package com.epam.e2e.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class E2EClientConfig {

    @Bean
    public RestClient resourceRestClient(@Value("${resource.service.url:http://localhost:8080}") String url) {
        return RestClient.builder().baseUrl(url).build();
    }

    @Bean
    public RestClient songRestClient(@Value("${song.service.url:http://localhost:8081}") String url) {
        return RestClient.builder().baseUrl(url).build();
    }
}
