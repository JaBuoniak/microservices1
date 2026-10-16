package com.epam.resourceservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

@Service
public class S3StorageService {

    private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageService(S3Client s3Client, @Value("${aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testConnectionAndInitBucket() {
        try {
            boolean bucketExists = s3Client.listBuckets().buckets().stream()
                    .anyMatch(b -> b.name().equals(bucketName));

            if (!bucketExists) {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
                log.info("Bucket '{}' did not exist and was successfully created in S3!", bucketName);
            } else {
                log.info("Bucket '{}' already exists in S3.", bucketName);
            }

            ListObjectsV2Response response = s3Client.listObjectsV2(
                    ListObjectsV2Request.builder().bucket(bucketName).build()
            );

            List<S3Object> files = response.contents();
            log.info("S3 Connection SUCCESS! Found {} files in bucket '{}'.", files.size(), bucketName);

        } catch (Exception e) {
            log.error("Failed to connect to S3 LocalStack: {}", e.getMessage(), e);
        }
    }
}
