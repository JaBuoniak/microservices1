package com.epam.resourceprocessor.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import com.epam.resourceprocessor.dto.SongMetadataDto;

@Component
public class SongServiceClient {

    private final RestClient restClient;

    public SongServiceClient(@Value("${song.service.url:http://localhost:8081/songs}") String songServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(songServiceUrl).build();
    }

    @Retryable(
        retryFor = { RestClientException.class },
        noRetryFor = { HttpClientErrorException.class },
        maxAttempts = 5,
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public void saveSongMetadata(SongMetadataDto metadata) {
        restClient.post().body(metadata).retrieve().toBodilessEntity();
    }
}
