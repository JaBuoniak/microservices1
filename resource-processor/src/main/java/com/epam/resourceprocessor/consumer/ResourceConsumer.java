package com.epam.resourceprocessor.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.epam.resourceprocessor.client.ResourceServiceClient;
import com.epam.resourceprocessor.client.SongServiceClient;
import com.epam.resourceprocessor.mapper.SongMetadataMapper;
import com.epam.resourceprocessor.service.Mp3MetadataExtractor;
import org.apache.tika.metadata.Metadata;

@Component
public class ResourceConsumer {

    private final ResourceServiceClient resourceServiceClient;
    private final SongServiceClient songServiceClient;
    private final Mp3MetadataExtractor metadataExtractor;
    private final SongMetadataMapper metadataMapper;

    public ResourceConsumer(
            ResourceServiceClient resourceServiceClient,
            SongServiceClient songServiceClient,
            Mp3MetadataExtractor metadataExtractor,
            SongMetadataMapper metadataMapper) {
        this.resourceServiceClient = resourceServiceClient;
        this.songServiceClient = songServiceClient;
        this.metadataExtractor = metadataExtractor;
        this.metadataMapper = metadataMapper;
    }

    @RabbitListener(queues = "${resource.queue}")
    public void processResource(Integer resourceId) {
        byte[] resource = resourceServiceClient.getResource(resourceId);
        Metadata rawMetadata = metadataExtractor.extract(resource);
        System.out.println(String.format("[%d]: %s", resourceId, rawMetadata));
        songServiceClient.saveSongMetadata(metadataMapper.toDto(resourceId, rawMetadata));
    }
}
