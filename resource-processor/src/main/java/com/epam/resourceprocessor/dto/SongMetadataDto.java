package com.epam.resourceprocessor.dto;

public record SongMetadataDto(
        Integer id,
        String name,
        String artist,
        String album,
        String duration,
        String year
) {
}
