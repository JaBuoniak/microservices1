package com.epam.e2e;

import com.epam.e2e.config.E2EClientConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = E2EClientConfig.class)
@TestPropertySource(locations = "classpath:application.properties")
class SmokeTest {

    @Autowired
    private RestClient resourceRestClient;

    @Autowired
    private RestClient songRestClient;

    @Test
    @DisplayName("should call Resource Service DELETE endpoint")
    void shouldCallResourceService() {
        ResponseEntity<String> response = resourceRestClient.delete()
                .uri("/resources?id=999999")
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"ids\":[]"));
    }

    @Test
    @DisplayName("should call Song Service DELETE endpoint")
    void shouldCallSongService() {
        ResponseEntity<String> response = songRestClient.delete()
                .uri("/songs?id=999999")
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"ids\":[]"));
    }
}
