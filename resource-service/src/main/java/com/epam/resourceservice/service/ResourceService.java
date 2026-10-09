package com.epam.resourceservice.service;

import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.repository.ResourceRepository;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final Mp3MetadataExtractor metadataExtractor;

    public ResourceService(ResourceRepository resourceRepository, Mp3MetadataExtractor metadataExtractor) {
        this.resourceRepository = resourceRepository;
        this.metadataExtractor = metadataExtractor;
    }

    public int createResource(byte[] data) {
        System.out.println("Received MP3 audio file, size: " + data.length + " bytes");

        Metadata metadata = metadataExtractor.extract(data);
        System.out.println("--- Parsed MP3 Metadata ---");
        for (String name : metadata.names()) {
            System.out.println(name + " = " + metadata.get(name));
        }
        System.out.println("---------------------------");

        Resource resource = new Resource(data);
        return resourceRepository.save(resource).getId();
    }
}
