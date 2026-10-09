package com.epam.resourceservice.controller;

import com.epam.resourceservice.dto.UploadResourceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @PostMapping(consumes = "audio/mpeg")
    public ResponseEntity<UploadResourceResponse> uploadResource(@RequestBody byte[] audioData) {
        System.out.println("Received MP3 audio file, size: " + audioData.length + " bytes");
        return ResponseEntity.ok(new UploadResourceResponse(1));
    }
}
