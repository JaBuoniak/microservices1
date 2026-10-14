package com.epam.resourceservice.client;

import com.epam.resourceservice.dto.SongMetadataDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class SongServiceClient {

    private final RestClient restClient;

    public SongServiceClient(RestClient.Builder restClientBuilder,
            @Value("${song.service.url:http://song-service/songs}") String songServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(songServiceUrl).build();
    }

    public void createSongMetadata(SongMetadataDto metadata) {
        try {
            restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(metadata)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            System.out.println("SongService POST /songs error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw e;
        }
    }

    public void deleteSongMetadata(String idsCsv) {
        try {
            restClient.delete()
                    .uri(uriBuilder -> uriBuilder.queryParam("id", idsCsv).build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            System.out.println("SongService DELETE /songs error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw e;
        }
    }
}
