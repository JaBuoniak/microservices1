package com.epam.resourceservice.component;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.epam.resourceservice.dto.UploadResourceResponse;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.S3StorageService;


public class ResourceStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private S3StorageService s3StorageService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired 
    private S3Client s3Client;

    @Value("${resource.queue}")
    private String queueName;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    
    private byte[] uploadedFileBytes;
    private ResponseEntity<UploadResourceResponse> uploadResponse;
    private ResponseEntity<?> latestResponse;
    private Integer createdResourceId;

    @Given("storage and database are clean")
    public void storage_and_database_are_clean() {
        resourceRepository.deleteAll();

        ListObjectsV2Response objectsResponse = s3Client.listObjectsV2(req -> req.bucket(bucketName));
        objectsResponse.contents().forEach(object -> s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(object.key())
                .build()));
    }

    @When("client uploads audio file {string}")
    public void client_uploads_audio_file(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        this.uploadedFileBytes = resource.getInputStream().readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("audio/mpeg"));

        HttpEntity<byte[]> request = new HttpEntity<>(this.uploadedFileBytes, headers);

        this.uploadResponse = restTemplate.postForEntity(
                "/resources",
                request,
                UploadResourceResponse.class
        );
        this.latestResponse = this.uploadResponse;

        if (this.uploadResponse.getBody() != null) {
            this.createdResourceId = this.uploadResponse.getBody().id();
        }
    }

    @Then("response status is {int}")
    public void response_status_is(Integer statusCode) {
        assertThat(latestResponse).isNotNull();
        assertThat(latestResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @Then("response contains valid resource ID")
    public void response_contains_valid_resource_id() {
        assertThat(createdResourceId).isNotNull().isPositive();
        assertThat(resourceRepository.existsById(createdResourceId)).isTrue();
    }

    @Then("S3 storage contains content of {string}")
    public void s3_storage_contains_content_of(String fileName) throws IOException {
        assertThat(createdResourceId).isNotNull();
        Resource resource = resourceRepository.findById(createdResourceId)
                .orElseThrow(() -> new AssertionError("Resource not found with ID: " + createdResourceId));

        byte[] actualBytes = s3StorageService.download(resource.getLocation());
        assertThat(actualBytes).isEqualTo(uploadedFileBytes);
    }

    @Then("message with resource ID is published to RabbitMQ")
    public void message_with_resource_id_is_published_to_rabbit_mq() {
        assertThat(createdResourceId).isNotNull();
        Object received = rabbitTemplate.receiveAndConvert(queueName, 5000L);
        assertThat(received).isNotNull();
        assertThat(received.toString()).isEqualTo(String.valueOf(createdResourceId));
    }

    @Given("resource with ID {int} exists with content of {string}")
    public void resource_with_id_exists_with_content_of(Integer int1, String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Given("song-service accepts metadata deletion for ID {int}")
    public void song_service_accepts_metadata_deletion_for_id(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("client deletes resource with ID {int}")
    public void client_deletes_resource_with_id(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("resource {int} is removed from database and S3")
    public void resource_is_removed_from_database_and_s3(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("song-service receives deletion request for ID {int}")
    public void song_service_receives_deletion_request_for_id(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
