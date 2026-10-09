package com.epam.resourceservice.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryRepository {
    private final Map<Integer, byte[]> inMemoryRepository = new HashMap<>();
    private int idCounter = 1;

    public int save(byte[] resource) {
        int id = idCounter++;
        inMemoryRepository.put(id, resource);
        return id;
    }
}
