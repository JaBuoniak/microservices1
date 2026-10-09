package com.epam.songservice.controller;

import com.epam.songservice.dto.CreateSongRequest;
import com.epam.songservice.dto.CreateSongResponse;
import com.epam.songservice.dto.DeleteSongsResponse;
import com.epam.songservice.dto.GetSongResponse;
import com.epam.songservice.service.SongService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<GetSongResponse> getSong(@PathVariable int id) {
        return ResponseEntity.ok(songService.getSong(id));
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongsResponse> deleteSongs(@RequestParam("id") String id) {
        List<Integer> deletedIds = songService.deleteSongs(id);
        return ResponseEntity.ok(new DeleteSongsResponse(deletedIds));
    }
}
