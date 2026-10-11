package com.epam.resourceservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.epam.resourceservice.client.SongServiceClient;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.InvalidCsvParameterException;
import com.epam.resourceservice.exception.InvalidResourceIdException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.mapper.SongMetadataMapper;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.dto.SongMetadataDto;
import org.apache.tika.metadata.Metadata;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final Mp3MetadataExtractor metadataExtractor;
    private final SongServiceClient songServiceClient;
    private final SongMetadataMapper songMetadataMapper;

    public ResourceService(ResourceRepository resourceRepository,
                           Mp3MetadataExtractor metadataExtractor,
                           SongServiceClient songServiceClient,
                           SongMetadataMapper songMetadataMapper) {
        this.resourceRepository = resourceRepository;
        this.metadataExtractor = metadataExtractor;
        this.songServiceClient = songServiceClient;
        this.songMetadataMapper = songMetadataMapper;
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

        try {
            SongMetadataDto songMetadata = songMetadataMapper.toDto(savedResource.getId(), metadata);
            songServiceClient.createSongMetadata(songMetadata);
        } catch (Exception e) {
            resourceRepository.delete(savedResource);
            throw e;
        }

        return savedResource.getId();
    }

    public byte[] getResource(int id) {
        if (id <= 0) {
            throw new InvalidResourceIdException("Invalid value '" + id + "' for ID. Must be a positive integer");
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
            throw new InvalidCsvParameterException("CSV string is too long: received " + idsCsv.length() + " characters, maximum allowed is 200");
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
                System.out.println("Failed to delete song metadata: " + e.getMessage());
            }
        }

        return existingIds;
    }

    private List<Integer> parseAndValidateIds(String idsCsv) {
        String[] values = idsCsv.split(",", -1);
        List<Integer> ids = new ArrayList<>();
        for (String value : values) {
            String trimmed = value.trim();
            try {
                int id = Integer.parseInt(trimmed);
                if (id <= 0) {
                    throw new InvalidCsvParameterException("Invalid ID format: '" + trimmed + "'. Only positive integers are allowed");
                }
                ids.add(id);
            } catch (NumberFormatException e) {
                throw new InvalidCsvParameterException("Invalid ID format: '" + trimmed + "'. Only positive integers are allowed");
            }
        }
        return ids;
    }
}
