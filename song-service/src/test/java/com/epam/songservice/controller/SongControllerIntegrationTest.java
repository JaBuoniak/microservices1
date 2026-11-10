package com.epam.songservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;


import com.epam.songservice.entity.Song;
import com.epam.songservice.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


@SpringBootTest
@AutoConfigureMockMvc
public class SongControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SongRepository songRepository;

    @BeforeEach
    void setUp() {
        songRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /songs - should create song and return 200 with ID")
    void postSongs_validPayload_shouldCreateSong() throws Exception {
        // given
        String requestJson = """
            {
                "id": 1,
                "name": "We Will Rock You",
                "artist": "Queen",
                "album": "News of the World",
                "duration": "02:02",
                "year": "1977"
            }
            """;

        // when
        ResultActions result = mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        assertThat(songRepository.findById(1)).isPresent();
    }

    @Test
    @DisplayName("POST /songs - should not create song and return 400 bad request")
    void postSongs_invalidPayload_shouldReturnBadRequest() throws Exception {
        // given
        String requestJson = """
            {
                "id": 1,
                "name": null,
                "artist": "Queen",
                "album": "News of the World",
                "duration": "02:02",
                "year": "1977"
            }
            """;

        // when
        ResultActions result = mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        result.andExpect(status().isBadRequest());
        assertThat(songRepository.findById(1)).isEmpty();
    }

    @Test
    @DisplayName("GET /songs/{id} - should return song details when exists")
    void getSongs_validId_shouldReturnSong() throws Exception {
        // given
        Song song = new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", "05:55", "1975");
        songRepository.save(song);

        // when
        ResultActions result = mockMvc.perform(get("/songs/{id}", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bohemian Rhapsody"))
                .andExpect(jsonPath("$.artist").value("Queen"))
                .andExpect(jsonPath("$.duration").value("05:55"))
                .andExpect(jsonPath("$.year").value("1975"));
    }

    @Test
    @DisplayName("GET /songs/{id} - should return 404 when song does not exist")
    void getSongs_invalidId_shouldReturnNotFound() throws Exception {
        // given

        // when
        ResultActions result = mockMvc.perform(get("/songs/{id}", 999));

        // then
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /songs - should return 409 Conflict when song with ID already exists")
    void postSongs_duplicateId_shouldReturnConflict() throws Exception {
        // given
        Song existingSong = new Song(1, "Existing", "Artist", "Album", "03:00", "2020");
        songRepository.save(existingSong);

        String duplicateJson = """
            {
                "id": 1,
                "name": "New Song",
                "artist": "Artist",
                "album": "Album",
                "duration": "03:00",
                "year": "2020"
            }
            """;

        // when
        ResultActions result = mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateJson));

        // then
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("409"));
    }

    @Test
    @DisplayName("GET /songs/{id} - should return 400 Bad Request when ID is not positive")
    void getSongs_nonPositiveId_shouldReturnBadRequest() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/songs/{id}", -1));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"));
    }

    @Test
    @DisplayName("DELETE /songs - should delete existing songs and return deleted IDs")
    void deleteSongs_validCsv_shouldDeleteAndReturnIds() throws Exception {
        // given
        songRepository.save(new Song(1, "Song 1", "Artist 1", "Album 1", "01:00", "2021"));
        songRepository.save(new Song(2, "Song 2", "Artist 2", "Album 2", "02:00", "2022"));

        // when
        ResultActions result = mockMvc.perform(delete("/songs").param("id", "1,2,99"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids[0]").value(1))
                .andExpect(jsonPath("$.ids[1]").value(2));

        assertThat(songRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("DELETE /songs - should return 400 when CSV format is invalid")
    void deleteSongs_invalidCsv_shouldReturnBadRequest() throws Exception {
        // when
        ResultActions result = mockMvc.perform(delete("/songs").param("id", "invalid,csv"));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"));
    }

    @Test
    @DisplayName("DELETE /songs - should return 400 when CSV string exceeds 200 characters")
    void deleteSongs_csvTooLong_shouldReturnBadRequest() throws Exception {
        // given
        String longCsv = "1,".repeat(101);

        // when
        ResultActions result = mockMvc.perform(delete("/songs").param("id", longCsv));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"));
    }
}
