package com.epam.resourceprocessor.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.epam.resourceprocessor.dto.SongMetadataDto;

@Component
public class SongServiceClient {

    private final RestClient restClient;

    public SongServiceClient(@Value("${song.service.url:http://localhost:8081/songs}") String songServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(songServiceUrl).build();
    }

    public void saveSongMetadata(SongMetadataDto metadata) {
        restClient.post().body(metadata).retrieve().toBodilessEntity();
    }
}
