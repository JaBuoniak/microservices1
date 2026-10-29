package com.epam.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnhappyPathStepDefinitions {

    @Autowired
    private RestClient resourceRestClient;

    @Autowired
    private RestClient songRestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseEntity<String> uploadResponse;
    private int resourceId;

    @When("User uploads invalid file {string} to Resource Service")
    public void uploadInvalidAudioFile(String fileName) throws Exception {
        byte[] audioData;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            assertNotNull(is, "File " + fileName + " must be present in classpath");
            audioData = is.readAllBytes();
        }

        uploadResponse = resourceRestClient.post()
                .uri("/resources")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(audioData)
                .retrieve()
                .toEntity(String.class);
    }

    @Then("Resource Service accepts upload with status 200")
    public void verifyUploadAccepted() throws Exception {
        assertEquals(HttpStatus.OK, uploadResponse.getStatusCode());
        JsonNode json = objectMapper.readTree(uploadResponse.getBody());
        assertTrue(json.has("id"), "Response must contain 'id' field");
        resourceId = json.get("id").asInt();
        assertTrue(resourceId > 0, "Generated ID must be positive integer");
    }

    @Then("within {int} seconds Song Service does NOT have metadata for this resource")
    public void verifyNoSongMetadata(int timeoutSeconds) {
        await()
                .during(Duration.ofSeconds(timeoutSeconds))
                .atMost(Duration.ofSeconds(timeoutSeconds + 1))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    assertThrows(HttpClientErrorException.NotFound.class, () -> {
                        songRestClient.get()
                                .uri("/songs/{id}", resourceId)
                                .retrieve()
                                .toBodilessEntity();
                    });
                });
    }

    @After
    public void cleanUp() {
        if (resourceId > 0) {
            try {
                resourceRestClient.delete()
                        .uri("/resources?id=" + resourceId)
                        .retrieve()
                        .toBodilessEntity();
            } catch (Exception ignored) {
            }
            resourceId = 0;
        }
    }
}
