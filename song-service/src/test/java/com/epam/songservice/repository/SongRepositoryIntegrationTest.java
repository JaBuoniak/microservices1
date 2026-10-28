package com.epam.songservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.epam.songservice.entity.Song;

@DataJpaTest
class SongRepositoryIntegrationTest {

    @Autowired
    private SongRepository songRepository;

    @Test
    @DisplayName("Should persist and retrieve song entity")
    void shouldSaveAndFindSong() {
        // given
        Song song = new Song(1, "We Will Rock You", "Queen", "News of the World", "02:02", "1977");

        // when
        songRepository.save(song);

        // then
        Optional<Song> found = songRepository.findById(1);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("We Will Rock You");
        assertThat(found.get().getArtist()).isEqualTo("Queen");
    }

    @Test
    @DisplayName("Should fail when saving song with null required field (database constraint check)")
    void shouldThrowExceptionWhenNameIsNull() {
        // given
        Song invalidSong = new Song(2, null, "Queen", "Album", "02:00", "1977");

        // when & then
        assertThatThrownBy(() -> songRepository.saveAndFlush(invalidSong))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should check if song exists by ID")
    void shouldCheckIfSongExistsById() {
        // given
        Song song = new Song(1, "Song", "Artist", "Album", "03:00", "2020");
        songRepository.save(song);

        // when & then
        assertThat(songRepository.existsById(1)).isTrue();
        assertThat(songRepository.existsById(999)).isFalse();
    }

    @Test
    @DisplayName("Should find and delete existing songs by IDs while keeping others intact")
    void shouldFindAndDeleteSongsByIds() {
        // given
        Song song1 = new Song(1, "Song 1", "Artist 1", "Album 1", "01:00", "2021");
        Song song2 = new Song(2, "Song 2", "Artist 2", "Album 2", "02:00", "2022");
        Song song3 = new Song(3, "Song 3", "Artist 3", "Album 3", "03:00", "2023");
        songRepository.saveAll(List.of(song1, song2, song3));

        // when
        List<Song> toDelete = songRepository.findAllById(List.of(1, 2, 99)); 
        songRepository.deleteAll(toDelete);

        // then
        assertThat(songRepository.existsById(1)).isFalse();
        assertThat(songRepository.existsById(2)).isFalse();
        assertThat(songRepository.existsById(3)).isTrue();
        assertThat(songRepository.count()).isEqualTo(1);
    }
}
