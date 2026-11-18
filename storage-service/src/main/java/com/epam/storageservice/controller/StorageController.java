package com.epam.storageservice.controller;

import com.epam.storageservice.dto.CreateStorageResponse;
import com.epam.storageservice.dto.StorageRequest;
import com.epam.storageservice.dto.StorageResponse;
import com.epam.storageservice.service.StorageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storages")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<CreateStorageResponse> createStorage(@Valid @RequestBody StorageRequest request) {
        return ResponseEntity.ok(storageService.createStorage(request));
    }

    @GetMapping
    public ResponseEntity<List<StorageResponse>> getAllStorages() {
        return ResponseEntity.ok(storageService.getAllStorages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageResponse> getStorageById(@PathVariable Integer id) {
        return ResponseEntity.ok(storageService.getStorageById(id));
    }

    @DeleteMapping
    public ResponseEntity<List<Integer>> deleteStorages(@RequestParam("id") String id) {
        return ResponseEntity.ok(storageService.deleteStorages(id));
    }
}
