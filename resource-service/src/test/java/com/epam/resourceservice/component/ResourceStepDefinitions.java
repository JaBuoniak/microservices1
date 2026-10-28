package com.epam.resourceservice.component;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.S3StorageService;

import io.cucumber.java.en.And;

public class ResourceStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private S3StorageService s3StorageService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${resource.queue}")
    private String queueName;

    @Given("storage and database are clean")
    public void storage_and_database_are_clean() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("client uploads audio file {string}")
    public void client_uploads_audio_file(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("response status is {int}")
    public void response_status_is(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("response contains valid resource ID")
    public void response_contains_valid_resource_id() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("S3 storage contains content of {string}")
    public void s3_storage_contains_content_of(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("message with resource ID is published to RabbitMQ")
    public void message_with_resource_id_is_published_to_rabbit_mq() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
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
