package com.epam.songservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateSongRequest(
        @NotNull(message = "ID is required")
        @Positive(message = "ID must be a positive integer")
        Integer id,

        @NotBlank(message = "Name is required")
        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        String name,

        @NotBlank(message = "Artist is required")
        @Size(min = 1, max = 100, message = "Artist must be between 1 and 100 characters")
        String artist,

        @NotBlank(message = "Album is required")
        @Size(min = 1, max = 100, message = "Album must be between 1 and 100 characters")
        String album,

        @NotBlank(message = "Duration is required")
        @Pattern(regexp = "^([0-5][0-9]):[0-5][0-9]$", message = "Duration must be in mm:ss format with leading zeros")
        String duration,

        @NotBlank(message = "Year is required")
        @Pattern(regexp = "^(19[0-9]{2}|20[0-9]{2})$", message = "Year must be between 1900 and 2099")
        String year
) {
}
