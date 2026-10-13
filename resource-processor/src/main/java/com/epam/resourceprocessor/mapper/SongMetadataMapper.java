package com.epam.resourceprocessor.mapper;

import com.epam.resourceprocessor.dto.SongMetadataDto;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SongMetadataMapper {

    private static final Pattern DURATION_PATTERN = Pattern.compile("^([0-9]{2}):[0-5][0-9]$");

    public SongMetadataDto toDto(Integer resourceId, Metadata metadata) {
        return new SongMetadataDto(
                resourceId,
                metadata.get("dc:title"),
                metadata.get("xmpDM:artist"),
                metadata.get("xmpDM:album"),
                formatDuration(metadata.get("xmpDM:duration")),
                metadata.get("xmpDM:releaseDate")
        );
    }

    private String formatDuration(String rawDuration) {
        if (rawDuration == null || rawDuration.isBlank()) {
            return null;
        }
        if (DURATION_PATTERN.matcher(rawDuration).matches()) {
            return rawDuration;
        }
        try {
            double totalSeconds = Double.parseDouble(rawDuration);
            int totalSec = (int) totalSeconds;
            int minutes = totalSec / 60;
            int seconds = totalSec % 60;

            return String.format("%02d:%02d", minutes, seconds);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
