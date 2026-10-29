Feature: Audio Resource Pipeline Unhappy Path

  Scenario: Upload invalid file and verify no song metadata is created
    When User uploads file "invalid.mp3" to Resource Service
    Then Resource Service returns status 200 with generated resource ID
    And within 3 seconds Song Service does NOT have metadata for this resource
