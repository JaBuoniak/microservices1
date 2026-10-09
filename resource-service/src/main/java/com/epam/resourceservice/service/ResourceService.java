package com.epam.resourceservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.InvalidCsvParameterException;
import com.epam.resourceservice.exception.InvalidResourceIdException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;
import org.apache.tika.metadata.Metadata;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final Mp3MetadataExtractor metadataExtractor;

    public ResourceService(ResourceRepository resourceRepository, Mp3MetadataExtractor metadataExtractor) {
        this.resourceRepository = resourceRepository;
        this.metadataExtractor = metadataExtractor;
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
        return resourceRepository.save(resource).getId();
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
