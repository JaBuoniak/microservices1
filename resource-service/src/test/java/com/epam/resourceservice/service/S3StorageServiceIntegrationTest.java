package com.epam.resourceservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.epam.resourceservice.config.S3Config;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@SpringBootTest(classes = {S3Config.class, S3StorageService.class})
@Testcontainers
class S3StorageServiceIntegrationTest {

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:3.8"))
        .withServices(LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.s3.endpoint", () -> localstack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        registry.add("aws.s3.region", localstack::getRegion);
        registry.add("aws.s3.access-key-id", localstack::getAccessKey);
        registry.add("aws.s3.secret-access-key", localstack::getSecretKey);
    }

    @Autowired
    private S3StorageService s3StorageService;

    @Autowired
    private S3Client s3Client;


    @Test
    @DisplayName("Should upload file to S3 and download identical bytes")
    void shouldUploadAndDownloadFile() {
        // given
        String key = "test/sample-audio.mp3";
        byte[] expectedBytes = "fake mp3 content for test".getBytes(StandardCharsets.UTF_8);

        // when
        s3StorageService.upload(key, expectedBytes);
        byte[] actualBytes = s3StorageService.download(key);

        // then
        assertThat(actualBytes).isEqualTo(expectedBytes);
    }

    @Test
    @DisplayName("Should delete file from S3 and throw NoSuchKeyException on subsequent download")
    void shouldDeleteFileFromS3() {
        // given
        String key = "test/to-delete.mp3";
        s3StorageService.upload(key, "data to delete".getBytes(StandardCharsets.UTF_8));
        
        // when
        s3StorageService.delete(key);
        
        // then
        assertThatThrownBy(() -> s3StorageService.download(key))
                .isInstanceOf(NoSuchKeyException.class);
    }
}
