Feature: Audio Resource Pipeline Unhappy Path

  Scenario: Upload invalid file and verify no song metadata is created
    When User uploads invalid file "invalid.mp3" to Resource Service
    Then Resource Service accepts upload with status 200
    And within 3 seconds Song Service does NOT have metadata for this resource
