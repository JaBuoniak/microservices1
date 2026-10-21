package com.epam.resourceprocessor.service;

import org.springframework.stereotype.Service;
import com.epam.resourceprocessor.client.ResourceServiceClient;
import com.epam.resourceprocessor.client.SongServiceClient;
import com.epam.resourceprocessor.util.Mp3MetadataExtractor;
import com.epam.resourceprocessor.util.SongMetadataMapper;
import org.apache.tika.metadata.Metadata;

@Service
public class ResourceProcessingService {

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
        
        byte[] resource = resourceServiceClient.getResource(resourceId);
        Metadata rawMetadata = metadataExtractor.extract(resource);
        System.out.println(String.format("[%d]: %s", resourceId, rawMetadata));
        songServiceClient.saveSongMetadata(SongMetadataMapper.toDto(resourceId, rawMetadata));
    }
    
}
