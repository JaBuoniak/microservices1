package com.epam.storageservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StorageRequest(
        @NotNull(message = "storageType is required")
        String storageType,

        @NotBlank(message = "bucket is required")
        String bucket,

        @NotBlank(message = "path is required")
        String path
) {}
