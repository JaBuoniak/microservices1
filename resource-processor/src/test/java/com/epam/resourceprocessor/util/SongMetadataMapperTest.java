package com.epam.resourceprocessor.util;

import com.epam.resourceprocessor.dto.SongMetadataDto;
import org.apache.tika.metadata.Metadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SongMetadataMapperTest {

    @Test
    @DisplayName("Should correctly map all metadata fields when metadata is complete")
    void shouldMapAllFieldsWhenMetadataIsComplete() {
        // given
        Integer resourceId = 42;
        Metadata metadata = new Metadata();
        metadata.set("dc:title", "We Will Rock You");
        metadata.set("xmpDM:artist", "Queen");
        metadata.set("xmpDM:album", "News of the World");
        metadata.set("xmpDM:duration", "122.0");
        metadata.set("xmpDM:releaseDate", "1977");

        // when
        SongMetadataDto dto = SongMetadataMapper.toDto(resourceId, metadata);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(42);
        assertThat(dto.name()).isEqualTo("We Will Rock You");
        assertThat(dto.artist()).isEqualTo("Queen");
        assertThat(dto.album()).isEqualTo("News of the World");
        assertThat(dto.duration()).isEqualTo("02:02");
        assertThat(dto.year()).isEqualTo("1977");
    }

    @ParameterizedTest(name = "Duration raw \"{0}\" should be formatted as \"{1}\"")
    @CsvSource({
            "195.4, 03:15",
            "60.0, 01:00",
            "5.0, 00:05",
            "0.0, 00:00",
            "03:45, 03:45",
            "12:30, 12:30"
    })
    void shouldFormatDurationFromSecondsOrPreservePattern(String rawDuration, String expectedDuration) {
        // given
        Metadata metadata = new Metadata();
        metadata.set("xmpDM:duration", rawDuration);

        // when
        SongMetadataDto dto = SongMetadataMapper.toDto(1, metadata);

        // then
        assertThat(dto.duration()).isEqualTo(expectedDuration);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid_number", "abc:def"})
    @DisplayName("Should return null duration when raw duration is empty, null or not parseable")
    void shouldReturnNullDurationForInvalidOrEmptyInput(String invalidDuration) {
        // given
        Metadata metadata = new Metadata();
        if (invalidDuration != null) {
            metadata.set("xmpDM:duration", invalidDuration);
        }

        // when
        SongMetadataDto dto = SongMetadataMapper.toDto(1, metadata);

        // then
        assertThat(dto.duration()).isNull();
    }

    @Test
    @DisplayName("Should map to null fields when metadata is empty but preserve resourceId")
    void shouldHandleEmptyMetadataGracefully() {
        // given
        Metadata emptyMetadata = new Metadata();

        // when
        SongMetadataDto dto = SongMetadataMapper.toDto(99, emptyMetadata);

        // then
        assertThat(dto.id()).isEqualTo(99);
        assertThat(dto.name()).isNull();
        assertThat(dto.artist()).isNull();
        assertThat(dto.album()).isNull();
        assertThat(dto.duration()).isNull();
        assertThat(dto.year()).isNull();
    }
}
