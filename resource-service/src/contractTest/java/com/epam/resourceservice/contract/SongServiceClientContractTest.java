package com.epam.resourceservice.contract;

import com.epam.resourceservice.client.SongServiceClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(
        classes = SongServiceClient.class,
        properties = "song.service.url=http://localhost:${stubrunner.runningstubs.song-service.port}/songs"
)
@AutoConfigureStubRunner(
        ids = "com.epam:song-service:+:stubs",
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
}
