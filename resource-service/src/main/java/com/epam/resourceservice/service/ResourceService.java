package com.epam.resourceservice.service;

import org.springframework.stereotype.Service;

import com.epam.resourceservice.repository.InMemoryRepository;

@Service
public class ResourceService {
    private InMemoryRepository inMemoryRepository;

    ResourceService(InMemoryRepository inMemoryRepository) {
        this.inMemoryRepository = inMemoryRepository;
    }

    public int createResource(byte[] data) {
        System.out.println("Received MP3 audio file, size: " + data.length + " bytes");
        return inMemoryRepository.save(data);
    }
}
