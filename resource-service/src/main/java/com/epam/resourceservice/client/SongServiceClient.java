package com.epam.resourceservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class SongServiceClient {

    private final RestClient restClient;

    public SongServiceClient(@Value("${song.service.url:http://localhost:8081/songs}") String songServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(songServiceUrl).build();
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
