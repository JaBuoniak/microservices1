package com.epam.resourceservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

@Service
public class S3StorageService {

    private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

    private final S3Client s3Client;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void upload(String bucket, String key, byte[] data) {
        ensureBucketExists(bucket);
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(data));
    }

    public byte[] download(String bucket, String key) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return s3Client.getObjectAsBytes(getRequest).asByteArray();
    }

    public void delete(String bucket, String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    public void move(String sourceBucket, String sourceKey, String targetBucket, String targetKey) {
        ensureBucketExists(targetBucket);
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(sourceBucket)
                .sourceKey(sourceKey)
                .destinationBucket(targetBucket)
                .destinationKey(targetKey)
                .build();

        s3Client.copyObject(copyRequest);
        delete(sourceBucket, sourceKey);
    }

    public void ensureBucketExists(String bucket) {
        try {
            boolean bucketExists = s3Client.listBuckets().buckets().stream()
                    .anyMatch(b -> b.name().equals(bucket));
            if (!bucketExists) {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
                log.info("Bucket '{}' did not exist and was successfully created in S3!", bucket);
            }
        } catch (Exception e) {
            log.warn("Could not ensure bucket '{}' exists: {}", bucket, e.getMessage());
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testConnection() {
        try {
            int bucketCount = s3Client.listBuckets().buckets().size();
            log.info("S3 Connection SUCCESS! Found {} buckets in S3.", bucketCount);
        } catch (Exception e) {
            log.error("Failed to connect to S3 LocalStack: {}", e.getMessage(), e);
        }
    }
}

