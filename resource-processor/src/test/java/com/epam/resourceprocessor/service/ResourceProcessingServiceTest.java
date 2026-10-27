package com.epam.resourceprocessor.service;

import com.epam.resourceprocessor.client.ResourceServiceClient;
import com.epam.resourceprocessor.client.SongServiceClient;
import com.epam.resourceprocessor.dto.SongMetadataDto;
import com.epam.resourceprocessor.util.Mp3MetadataExtractor;
import org.apache.tika.metadata.Metadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceProcessingServiceTest {

    @Mock
    private ResourceServiceClient resourceServiceClient;

    @Mock
    private SongServiceClient songServiceClient;

    @Mock
    private Mp3MetadataExtractor metadataExtractor;

    @InjectMocks
    private ResourceProcessingService resourceProcessingService;

    @Test
    @DisplayName("Should coordinate fetching resource, extracting metadata and saving song metadata")
    void shouldProcessResourceSuccessfully() {
        // given
        Integer resourceId = 123;
        byte[] fakeAudioBytes = new byte[]{1, 2, 3};

        Metadata metadata = new Metadata();
        metadata.set("dc:title", "Bohemian Rhapsody");
        metadata.set("xmpDM:artist", "Queen");
        metadata.set("xmpDM:album", "A Night at the Opera");
        metadata.set("xmpDM:duration", "354.0");
        metadata.set("xmpDM:releaseDate", "1975");

        when(resourceServiceClient.getResource(resourceId)).thenReturn(fakeAudioBytes);
        when(metadataExtractor.extract(fakeAudioBytes)).thenReturn(metadata);

        // when
        resourceProcessingService.process(resourceId);

        // then
        verify(resourceServiceClient).getResource(resourceId);
        verify(metadataExtractor).extract(fakeAudioBytes);

        ArgumentCaptor<SongMetadataDto> captor = ArgumentCaptor.forClass(SongMetadataDto.class);
        verify(songServiceClient).saveSongMetadata(captor.capture());

        SongMetadataDto savedDto = captor.getValue();
        assertThat(savedDto.id()).isEqualTo(resourceId);
        assertThat(savedDto.name()).isEqualTo("Bohemian Rhapsody");
        assertThat(savedDto.artist()).isEqualTo("Queen");
        assertThat(savedDto.duration()).isEqualTo("05:54");
    }
}
