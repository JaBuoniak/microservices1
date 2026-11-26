package com.epam.storageservice.service;

import com.epam.storageservice.dto.CreateStorageResponse;
import com.epam.storageservice.dto.StorageRequest;
import com.epam.storageservice.dto.StorageResponse;
import com.epam.storageservice.entity.Storage;
import com.epam.storageservice.exception.InvalidCsvParameterException;
import com.epam.storageservice.exception.StorageAlreadyExistsException;
import com.epam.storageservice.exception.StorageNotFoundException;
import com.epam.storageservice.repository.StorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);
    private final StorageRepository storageRepository;

    public StorageService(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    public CreateStorageResponse createStorage(StorageRequest request) {
        if (storageRepository.existsByType(request.storageType())) {
            throw new StorageAlreadyExistsException("Storage with type '" + request.storageType() + "' already exists");
        }
        Storage storage = new Storage(request.storageType(), request.bucket(), request.path());
        Storage saved = storageRepository.save(storage);
        return new CreateStorageResponse(saved.getId());
    }

    public List<StorageResponse> getAllStorages() {
        List<StorageResponse> storages = storageRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
        log.info("Retrieved storages: count={}", storages.size());
        return storages;
    }

    public StorageResponse getStorageById(Integer id) {
        return storageRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new StorageNotFoundException("Storage with ID=" + id + " not found"));
    }

    public List<Integer> deleteStorages(String idsCsv) {
        if (idsCsv == null || idsCsv.isBlank()) {
            throw new InvalidCsvParameterException("CSV string must not be empty");
        }
        if (idsCsv.length() >= 200) {
            throw new InvalidCsvParameterException("CSV string is too long: received " + idsCsv.length() + " characters, maximum allowed is less than 200");
        }

        List<Integer> ids = parseAndValidateIds(idsCsv);
        List<Storage> existing = storageRepository.findAllById(ids);
        List<Integer> existingIds = existing.stream().map(Storage::getId).toList();

        storageRepository.deleteAll(existing);
        return existingIds;
    }

    private StorageResponse toResponse(Storage storage) {
        return new StorageResponse(
                storage.getId(),
                storage.getType(),
                storage.getBucket(),
                storage.getPath()
        );
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
