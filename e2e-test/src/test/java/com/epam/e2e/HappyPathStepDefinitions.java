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

public class HappyPathStepDefinitions {

    @Autowired
    private RestClient resourceRestClient;

    @Autowired
    private RestClient songRestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseEntity<String> uploadResponse;
    private ResponseEntity<String> deleteResponse;
    private int resourceId;

    @When("User uploads file {string} to Resource Service")
    public void uploadAudioFile(String fileName) throws Exception {
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

        if (uploadResponse.getStatusCode().is2xxSuccessful() && uploadResponse.getBody() != null) {
            JsonNode json = objectMapper.readTree(uploadResponse.getBody());
            if (json.has("id")) {
                resourceId = json.get("id").asInt();
            }
        }
    }

    @Then("Resource Service returns status 200 with generated resource ID")
    public void verifyUploadSuccess() throws Exception {
        assertEquals(HttpStatus.OK, uploadResponse.getStatusCode());
        JsonNode json = objectMapper.readTree(uploadResponse.getBody());
        assertTrue(json.has("id"), "Response must contain 'id' field");
        resourceId = json.get("id").asInt();
        assertTrue(resourceId > 0, "Generated ID must be positive integer");
    }

    @Then("within {int} seconds Song Service has metadata for this resource")
    public void verifySongMetadata(int timeoutSeconds) {
        await()
                .atMost(Duration.ofSeconds(timeoutSeconds))
                .pollInterval(Duration.ofMillis(500))
                .ignoreExceptionsInstanceOf(HttpClientErrorException.NotFound.class)
                .untilAsserted(() -> {
                    ResponseEntity<String> songResponse = songRestClient.get()
                            .uri("/songs/{id}", resourceId)
                            .retrieve()
                            .toEntity(String.class);

                    assertEquals(HttpStatus.OK, songResponse.getStatusCode());
                    JsonNode songJson = objectMapper.readTree(songResponse.getBody());
                    assertEquals(resourceId, songJson.get("id").asInt());
                });
    }

    @When("User deletes this resource from Resource Service")
    public void deleteAudioResource() {
        deleteResponse = resourceRestClient.delete()
                .uri("/resources?id=" + resourceId)
                .retrieve()
                .toEntity(String.class);
    }

    @Then("Resource Service returns status 200 with deleted resource ID")
    public void verifyDeleteSuccess() throws Exception {
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        JsonNode json = objectMapper.readTree(deleteResponse.getBody());
        assertTrue(json.has("ids"), "Response must contain 'ids' field");
        JsonNode idsArray = json.get("ids");
        assertTrue(idsArray.isArray(), "'ids' must be an array");
        boolean containsId = false;
        for (JsonNode idNode : idsArray) {
            if (idNode.asInt() == resourceId) {
                containsId = true;
                break;
            }
        }
        assertTrue(containsId, "Deleted ids must contain resource ID=" + resourceId);
    }

    @Then("Resource Service returns 404 for this resource")
    public void verifyResourceNotFound() {
        assertThrows(HttpClientErrorException.NotFound.class, () -> {
            resourceRestClient.get()
                    .uri("/resources/{id}", resourceId)
                    .retrieve()
                    .toBodilessEntity();
        });
    }

    @Then("Song Service returns 404 for this resource")
    public void verifySongNotFound() {
        await()
                .atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(200))
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
            try {
                songRestClient.delete()
                        .uri("/songs?id=" + resourceId)
                        .retrieve()
                        .toBodilessEntity();
            } catch (Exception ignored) {
            }
            resourceId = 0;
        }
    }
}
