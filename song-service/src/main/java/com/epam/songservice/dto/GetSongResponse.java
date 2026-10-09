package com.epam.songservice.dto;

public record GetSongResponse(
        Integer id,
        String name,
        String artist,
        String album,
        String duration,
        String year
) {
}
