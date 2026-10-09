package com.epam.resourceservice.service;

import org.springframework.stereotype.Service;

import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.repository.ResourceRepository;

@Service
public class ResourceService {
    private ResourceRepository resourceRepository;

    ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public int createResource(byte[] data) {
        System.out.println("Received MP3 audio file, size: " + data.length + " bytes");
        Resource resource = new Resource(data);
        return resourceRepository.save(resource).getId();
    }
}
