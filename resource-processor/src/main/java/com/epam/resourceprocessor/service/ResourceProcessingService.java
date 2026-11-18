package com.epam.resourceprocessor.service;

import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import com.epam.resourceprocessor.client.ResourceServiceClient;
import com.epam.resourceprocessor.client.SongServiceClient;
import com.epam.resourceprocessor.exception.InvalidAudioFormatException;
import com.epam.resourceprocessor.util.Mp3MetadataExtractor;
import com.epam.resourceprocessor.util.SongMetadataMapper;

@Service
public class ResourceProcessingService {

    private static final Logger log = LoggerFactory.getLogger(ResourceProcessingService.class);

    private final ResourceServiceClient resourceServiceClient;
    private final SongServiceClient songServiceClient;
    private final Mp3MetadataExtractor metadataExtractor;
    private final RabbitTemplate rabbitTemplate;

    @Value("${resource.processed.queue}")
    private String processedQueueName;

    public ResourceProcessingService(
            ResourceServiceClient resourceServiceClient,
            SongServiceClient songServiceClient,
            Mp3MetadataExtractor metadataExtractor,
            RabbitTemplate rabbitTemplate) {
        this.resourceServiceClient = resourceServiceClient;
        this.songServiceClient = songServiceClient;
        this.metadataExtractor = metadataExtractor;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void process(Integer resourceId) {
        try {
            byte[] resource = resourceServiceClient.getResource(resourceId);
            Metadata rawMetadata = metadataExtractor.extract(resource);
            log.info("[{}]: {}", resourceId, rawMetadata);
            songServiceClient.saveSongMetadata(SongMetadataMapper.toDto(resourceId, rawMetadata));
            rabbitTemplate.convertAndSend(processedQueueName, String.valueOf(resourceId));
            log.info("Successfully processed resource ID={}, notification sent to queue '{}'", resourceId, processedQueueName);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Resource with ID={} not found in Resource Service (possibly deleted), skipping processing.", resourceId);
        } catch (HttpClientErrorException e) {
            log.warn("Client error ({}) while fetching resource with ID={}, skipping: {}", e.getStatusCode(), resourceId, e.getMessage());
        } catch (InvalidAudioFormatException e) {
            log.warn("Resource with ID={} has invalid audio format, skipping song metadata creation: {}", resourceId, e.getMessage());
        }
    }
}
