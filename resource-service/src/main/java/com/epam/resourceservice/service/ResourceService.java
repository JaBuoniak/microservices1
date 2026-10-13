package com.epam.resourceservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.epam.resourceservice.client.SongServiceClient;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.InvalidAudioFormatException;
import com.epam.resourceservice.exception.InvalidCsvParameterException;
import com.epam.resourceservice.exception.InvalidResourceIdException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final SongServiceClient songServiceClient;
    private final S3StorageService s3StorageService;

    public ResourceService(ResourceRepository resourceRepository,
                           SongServiceClient songServiceClient,
                           S3StorageService s3StorageService) {
        this.resourceRepository = resourceRepository;
        this.songServiceClient = songServiceClient;
        this.s3StorageService = s3StorageService;
    }

    public int createResource(byte[] data) {
        if (data == null || data.length == 0) {
            throw new InvalidAudioFormatException("The request body is invalid MP3: body is empty");
        }

        String location = "resources/" + UUID.randomUUID() + ".mp3";
        s3StorageService.upload(location, data);

        Resource resource = new Resource(location);
        Resource savedResource = resourceRepository.save(resource);

        return savedResource.getId();
    }

    public byte[] getResource(int id) {
        if (id <= 0) {
            throw new InvalidResourceIdException("Invalid value '" + id + "' for ID. Must be a positive integer");
        }

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID=" + id + " not found"));

        return s3StorageService.download(resource.getLocation());
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

        for (Resource resource : existingResources) {
            s3StorageService.delete(resource.getLocation());
        }

        resourceRepository.deleteAll(existingResources);

        if (!existingIds.isEmpty()) {
            String deletedCsv = String.join(",", existingIds.stream().map(String::valueOf).toList());
            songServiceClient.deleteSongMetadata(deletedCsv);
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
