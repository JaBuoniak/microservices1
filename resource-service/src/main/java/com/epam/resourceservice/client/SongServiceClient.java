package com.epam.resourceservice.client;

import com.epam.resourceservice.dto.SongMetadataDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SongServiceClient {

    private final RestClient restClient;

    public SongServiceClient(@Value("${song.service.url:http://localhost:8081/songs}") String songServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(songServiceUrl).build();
    }

    public void createSongMetadata(SongMetadataDto metadata) {
        restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(metadata)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteSongMetadata(String idsCsv) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder.queryParam("id", idsCsv).build())
                .retrieve()
                .toBodilessEntity();
    }
}
