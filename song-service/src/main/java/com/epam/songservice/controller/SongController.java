package com.epam.songservice.controller;

import com.epam.songservice.dto.CreateSongRequest;
import com.epam.songservice.dto.CreateSongResponse;
import com.epam.songservice.service.SongService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping
    public ResponseEntity<CreateSongResponse> createSong(@Valid @RequestBody CreateSongRequest request) {
        return ResponseEntity.ok(songService.createSong(request));
    }
}
