package com.epam.resourceprocessor.util;

import com.epam.resourceprocessor.exception.InvalidAudioFormatException;
import org.apache.tika.metadata.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mp3MetadataExtractorTest {

    private Mp3MetadataExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new Mp3MetadataExtractor();
    }

    @Test
    @DisplayName("Should throw InvalidAudioFormatException when audio byte array is null")
    void shouldThrowExceptionWhenAudioDataIsNull() {
        assertThatThrownBy(() -> extractor.extract(null))
                .isInstanceOf(InvalidAudioFormatException.class)
                .hasMessageContaining("body is empty");
    }

    @Test
    @DisplayName("Should throw InvalidAudioFormatException when audio byte array is empty")
    void shouldThrowExceptionWhenAudioDataIsEmpty() {
        assertThatThrownBy(() -> extractor.extract(new byte[0]))
                .isInstanceOf(InvalidAudioFormatException.class)
                .hasMessageContaining("body is empty");
    }

    @Test
    @DisplayName("Should throw InvalidAudioFormatException when provided file is PNG instead of MP3")
    void shouldThrowExceptionWhenFileIsNotMp3() throws IOException {
        byte[] pngData = loadResource("invalid-type.png");

        assertThatThrownBy(() -> extractor.extract(pngData))
                .isInstanceOf(InvalidAudioFormatException.class)
                .hasMessage("The request body is invalid MP3");
    }

    @Test
    @DisplayName("Should successfully extract metadata from valid MP3 file")
    void shouldExtractMetadataFromValidMp3() throws IOException {
        byte[] mp3Data = loadResource("valid-sample-with-required-tags.mp3");

        Metadata metadata = extractor.extract(mp3Data);

        assertThat(metadata).isNotNull();
        assertThat(metadata.get("dc:title")).isEqualTo("Test Title");
        assertThat(metadata.get("xmpDM:artist")).isEqualTo("Test Artist");
        assertThat(metadata.get("xmpDM:album")).isEqualTo("Test Album");
        assertThat(metadata.get("xmpDM:releaseDate")).isEqualTo("2025");
        assertThat(metadata.get("xmpDM:audioCompressor")).isEqualTo("MP3");
    }

    @Test
    @DisplayName("Should extract metadata with blank artist and album for sample with missed tags")
    void shouldExtractMetadataWhenId3TagsAreMissing() throws IOException {
        byte[] mp3Data = loadResource("invalid-sample-with-missed-tags.mp3");

        Metadata metadata = extractor.extract(mp3Data);

        assertThat(metadata).isNotNull();
        assertThat(metadata.get("dc:title")).isEqualTo("Valid Title");
        assertThat(metadata.get("xmpDM:artist")).isBlank();
        assertThat(metadata.get("xmpDM:album")).isBlank();
        assertThat(metadata.get("xmpDM:audioCompressor")).isEqualTo("MP3");
    }

    private byte[] loadResource(String fileName) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Test resource file not found: " + fileName);
            }
            return inputStream.readAllBytes();
        }
    }
}
