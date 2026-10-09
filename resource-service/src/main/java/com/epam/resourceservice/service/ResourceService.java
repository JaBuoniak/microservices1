package com.epam.resourceservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.epam.resourceservice.client.SongServiceClient;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.InvalidCsvParameterException;
import com.epam.resourceservice.exception.InvalidResourceIdException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.dto.SongMetadataDto;
import org.apache.tika.metadata.Metadata;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final Mp3MetadataExtractor metadataExtractor;
    private final SongServiceClient songServiceClient;

    public ResourceService(ResourceRepository resourceRepository,
                           Mp3MetadataExtractor metadataExtractor,
                           SongServiceClient songServiceClient) {
        this.resourceRepository = resourceRepository;
        this.metadataExtractor = metadataExtractor;
        this.songServiceClient = songServiceClient;
    }

    public int createResource(byte[] data) {
        System.out.println("Received MP3 audio file, size: " + data.length + " bytes");

        Metadata metadata = metadataExtractor.extract(data);
        System.out.println("--- Parsed MP3 Metadata ---");
        for (String name : metadata.names()) {
            System.out.println(name + " = " + metadata.get(name));
        }
        System.out.println("---------------------------");

        Resource resource = new Resource(data);
        Resource savedResource = resourceRepository.save(resource);

        SongMetadataDto songMetadata = new SongMetadataDto(
                savedResource.getId(),
                metadata.get("dc:title"),
                metadata.get("xmpDM:artist"),
                metadata.get("xmpDM:album"),
                metadata.get("xmpDM:duration"),
                metadata.get("xmpDM:releaseDate")
        );

        songServiceClient.createSongMetadata(songMetadata);

        return savedResource.getId();
    }

    public byte[] getResource(int id) {
        if (id <= 0) {
            throw new InvalidResourceIdException("Invalid resource ID: " + id + ". ID must be a positive integer");
        }

        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID=" + id + " not found"))
                .getData();
    }

    public List<Integer> deleteResources(String idsCsv) {
        if (idsCsv == null || idsCsv.isBlank()) {
            throw new InvalidCsvParameterException("CSV string must not be empty");
        }

        if (idsCsv.length() > 200) {
            throw new InvalidCsvParameterException("CSV string length must not exceed 200 characters");
        }

        List<Integer> ids = parseAndValidateIds(idsCsv);
        List<Resource> existingResources = resourceRepository.findAllById(ids);
        List<Integer> existingIds = existingResources.stream().map(Resource::getId).toList();

        resourceRepository.deleteAll(existingResources);

        if (!existingIds.isEmpty()) {
            String deletedCsv = String.join(",", existingIds.stream().map(String::valueOf).toList());
            try {
                songServiceClient.deleteSongMetadata(deletedCsv);
            } catch (Exception e) {
                System.err.println("Failed to delete song metadata: " + e.getMessage());
            }
        }

        return existingIds;
    }

    private List<Integer> parseAndValidateIds(String idsCsv) {
        String[] values = idsCsv.split(",", -1);
        List<Integer> ids = new ArrayList<>();
        for (String value : values) {
            try {
                int id = Integer.parseInt(value.trim());
                if (id <= 0) {
                    throw new InvalidCsvParameterException("Invalid CSV format for IDs: '" + idsCsv + "'");
                }
                ids.add(id);
            } catch (NumberFormatException e) {
                throw new InvalidCsvParameterException("Invalid CSV format for IDs: '" + idsCsv + "'");
            }
        }
        return ids;
    }
}
