package com.epam.songservice.service;

import com.epam.songservice.dto.CreateSongRequest;
import com.epam.songservice.dto.CreateSongResponse;
import com.epam.songservice.exception.SongAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SongService {

    private final Map<Integer, CreateSongRequest> storage = new ConcurrentHashMap<>();

    public CreateSongResponse createSong(CreateSongRequest request) {
        if (storage.containsKey(request.id())) {
            throw new SongAlreadyExistsException("Metadata with the same ID already exists");
        }

        storage.put(request.id(), request);
        return new CreateSongResponse(request.id());
    }
}
