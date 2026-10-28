Feature: Resource Service Management

  Scenario: Successfully upload audio resource
    Given storage and database are clean
    When client uploads audio file "test.mp3"
    Then response status is 200
    And response contains valid resource ID
    And S3 storage contains content of "test.mp3"
    And message with resource ID is published to RabbitMQ

  Scenario: Successfully delete audio resource
    Given resource with ID 1 exists with content of "test.mp3"
    And song-service accepts metadata deletion for ID 1
    When client deletes resource with ID 1
    Then response status is 200
    And resource 1 is removed from database and S3
    And song-service receives deletion request for ID 1

