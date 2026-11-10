package com.epam.resourceprocessor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import com.epam.resourceprocessor.client.ResourceServiceClient;
import com.epam.resourceprocessor.client.SongServiceClient;
import com.epam.resourceprocessor.exception.InvalidAudioFormatException;
import com.epam.resourceprocessor.util.Mp3MetadataExtractor;
import com.epam.resourceprocessor.util.SongMetadataMapper;
import org.apache.tika.metadata.Metadata;

@Service
public class ResourceProcessingService {

    private static final Logger log = LoggerFactory.getLogger(ResourceProcessingService.class);

    private final ResourceServiceClient resourceServiceClient;
    private final SongServiceClient songServiceClient;
    private final Mp3MetadataExtractor metadataExtractor;

    public ResourceProcessingService(
            ResourceServiceClient resourceServiceClient,
            SongServiceClient songServiceClient,
            Mp3MetadataExtractor metadataExtractor) {
        this.resourceServiceClient = resourceServiceClient;
        this.songServiceClient = songServiceClient;
        this.metadataExtractor = metadataExtractor;
    }

    public void process(Integer resourceId) {
        try {
            byte[] resource = resourceServiceClient.getResource(resourceId);
            Metadata rawMetadata = metadataExtractor.extract(resource);
            log.info("[{}]: {}", resourceId, rawMetadata);
            songServiceClient.saveSongMetadata(SongMetadataMapper.toDto(resourceId, rawMetadata));
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Resource with ID={} not found in Resource Service (possibly deleted), skipping processing.", resourceId);
        } catch (HttpClientErrorException e) {
            log.warn("Client error ({}) while fetching resource with ID={}, skipping: {}", e.getStatusCode(), resourceId, e.getMessage());
        } catch (InvalidAudioFormatException e) {
            log.warn("Resource with ID={} has invalid audio format, skipping song metadata creation: {}", resourceId, e.getMessage());
        }
    }
}
