Feature: Audio Resource Pipeline Happy Path

  Scenario: Successfully upload MP3 file and verify asynchronous processing
    When User uploads file "valid.mp3" to Resource Service
    Then Resource Service returns status 200 with generated resource ID
    And within 10 seconds Song Service has metadata for this resource

  Scenario: Successfully delete MP3 file and verify cascade deletion
    Given User uploads file "valid.mp3" to Resource Service
    And Resource Service returns status 200 with generated resource ID
    And within 10 seconds Song Service has metadata for this resource
    When User deletes this resource from Resource Service
    Then Resource Service returns status 200 with deleted resource ID
    And Resource Service returns 404 for this resource
    And Song Service returns 404 for this resource

