package com.epam.songservice.service;

import com.epam.songservice.dto.CreateSongRequest;
import com.epam.songservice.dto.CreateSongResponse;
import com.epam.songservice.entity.Song;
import com.epam.songservice.exception.SongAlreadyExistsException;
import com.epam.songservice.repository.SongRepository;
import org.springframework.stereotype.Service;

@Service
public class SongService {

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public CreateSongResponse createSong(CreateSongRequest request) {
        if (songRepository.existsById(request.id())) {
            throw new SongAlreadyExistsException("Metadata with the same ID already exists");
        }

        Song song = new Song(
                request.id(),
                request.name(),
                request.artist(),
                request.album(),
                request.duration(),
                request.year()
        );

        Song savedSong = songRepository.save(song);
        return new CreateSongResponse(savedSong.getId());
    }
}
