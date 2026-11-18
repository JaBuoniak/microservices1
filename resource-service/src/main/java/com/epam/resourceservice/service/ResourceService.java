package com.epam.resourceservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.epam.resourceservice.client.SongServiceClient;
import com.epam.resourceservice.client.StorageServiceClient;
import com.epam.resourceservice.dto.StorageResponse;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.InvalidAudioFormatException;
import com.epam.resourceservice.exception.InvalidCsvParameterException;
import com.epam.resourceservice.exception.InvalidResourceIdException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;

@Service
public class ResourceService {

    public static final String PERMANENT_STORAGE_TYPE = "PERMANENT";
    public static final String STAGING_STORAGE_TYPE = "STAGING";
    private final ResourceRepository resourceRepository;
    private final SongServiceClient songServiceClient;
    private final StorageServiceClient storageServiceClient;
    private final S3StorageService s3StorageService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${resource.queue}")
    private String queueName;

    public ResourceService(ResourceRepository resourceRepository,
                           SongServiceClient songServiceClient,
                           StorageServiceClient storageServiceClient,
                           S3StorageService s3StorageService,
                           RabbitTemplate rabbitTemplate) {
        this.resourceRepository = resourceRepository;
        this.songServiceClient = songServiceClient;
        this.storageServiceClient = storageServiceClient;
        this.s3StorageService = s3StorageService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public int createResource(byte[] data) {
        if (data == null || data.length == 0) {
            throw new InvalidAudioFormatException("The request body is invalid MP3: body is empty");
        }

        StorageResponse stagingStorage = storageServiceClient.getStorageByType(STAGING_STORAGE_TYPE);
        String path = stagingStorage.path() != null ? stagingStorage.path().replaceAll("^/+", "").replaceAll("/+$", "") : "";
        String filename = UUID.randomUUID() + ".mp3";
        String location = path.isEmpty() ? filename : path + "/" + filename;

        s3StorageService.upload(stagingStorage.bucket(), location, data);

        Resource resource = new Resource(location, STAGING_STORAGE_TYPE, stagingStorage.bucket());
        Resource savedResource = resourceRepository.save(resource);

        rabbitTemplate.convertAndSend(queueName, String.valueOf(savedResource.getId()));

        return savedResource.getId();
    }

    public byte[] getResource(int id) {
        if (id <= 0) {
            throw new InvalidResourceIdException("Invalid value '" + id + "' for ID. Must be a positive integer");
        }

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID=" + id + " not found"));

        return s3StorageService.download(resource.getBucket(), resource.getLocation());
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
            s3StorageService.delete(resource.getBucket(), resource.getLocation());
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

    public void promoteToPermanent(Integer resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID=" + resourceId + " not found"));

        if (PERMANENT_STORAGE_TYPE.equals(resource.getState())) {
            return;
        }

        StorageResponse permanentStorage = storageServiceClient.getStorageByType(PERMANENT_STORAGE_TYPE);

        String oldLocation = resource.getLocation();
        String filename = oldLocation.contains("/")
                ? oldLocation.substring(oldLocation.lastIndexOf("/") + 1)
                : oldLocation;

        String targetPath = permanentStorage.path() != null
                ? permanentStorage.path().replaceAll("^/+", "").replaceAll("/+$", "")
                : "";

        String newLocation = targetPath.isEmpty() ? filename : targetPath + "/" + filename;

        s3StorageService.move(resource.getBucket(), oldLocation, permanentStorage.bucket(), newLocation);

        resource.setState(PERMANENT_STORAGE_TYPE);
        resource.setBucket(permanentStorage.bucket());
        resource.setLocation(newLocation);
        resourceRepository.save(resource);
    }
}
