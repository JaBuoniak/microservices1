package com.epam.resourceservice.dto;

public record SongMetadataDto(
        Integer id,
        String name,
        String artist,
        String album,
        String duration,
        String year
) {
}
