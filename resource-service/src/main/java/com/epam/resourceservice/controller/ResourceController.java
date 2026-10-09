package com.epam.resourceservice.controller;

import com.epam.resourceservice.dto.UploadResourceResponse;
import com.epam.resourceservice.service.ResourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(consumes = "audio/mpeg")
    public ResponseEntity<UploadResourceResponse> uploadResource(@RequestBody byte[] audioData) {
        int resourceId = resourceService.createResource(audioData);
        return ResponseEntity.ok(new UploadResourceResponse(resourceId));
    }
}
