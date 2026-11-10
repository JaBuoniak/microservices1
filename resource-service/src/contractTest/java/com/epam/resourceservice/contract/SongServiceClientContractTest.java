package com.epam.resourceservice.contract;

import com.epam.resourceservice.client.SongServiceClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = SongServiceClient.class,
        properties = "song.service.url=http://localhost:${stubrunner.runningstubs.song-service.port}/songs"
)
@AutoConfigureStubRunner(
        ids = "com.epam:song-service:1.0-SNAPSHOT:stubs",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class SongServiceClientContractTest {

    @Autowired
    private SongServiceClient songServiceClient;

    @Test
    @DisplayName("should successfully delete single song metadata using contract stub")
    void shouldDeleteSingleSong() {
        assertDoesNotThrow(() -> songServiceClient.deleteSongMetadata("1"));
    }

    @Test
    @DisplayName("should successfully delete multiple songs metadata using contract stub")
    void shouldDeleteMultipleSongs() {
        assertDoesNotThrow(() -> songServiceClient.deleteSongMetadata("1,2"));
    }

    @Test
    @DisplayName("should handle non-existent song ID returning empty list without errors")
    void shouldHandleNonExistentSong() {
        assertDoesNotThrow(() -> songServiceClient.deleteSongMetadata("999"));
    }

    @Test
    @DisplayName("should throw BadRequest when song ID format is invalid")
    void shouldThrowBadRequestWhenSongIdIsInvalid() {
        HttpClientErrorException.BadRequest exception = assertThrows(
                HttpClientErrorException.BadRequest.class,
                () -> songServiceClient.deleteSongMetadata("invalid")
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getResponseBodyAsString().contains("Invalid ID format"));
        assertTrue(exception.getResponseBodyAsString().contains("400"));
    }

    @Test
    @DisplayName("should throw BadRequest when CSV string exceeds 200 characters")
    void shouldThrowBadRequestWhenCsvTooLong() {
        String longCsv = "1,3,5,7,9,12,15,18,21,24,27,30,33,36,39,42,45,48,51,54,57,60,63,66,69,72,75,78,81,84,87,90,93,96,99,103,107,111,115,119,123,127,131,135,139,143,147,151,155,159,163,167,171,175,179,183,187,191,195,199,203";
        HttpClientErrorException.BadRequest exception = assertThrows(
                HttpClientErrorException.BadRequest.class,
                () -> songServiceClient.deleteSongMetadata(longCsv)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getResponseBodyAsString().contains("CSV string is too long"));
        assertTrue(exception.getResponseBodyAsString().contains("400"));
    }
}
