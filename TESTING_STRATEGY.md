# Testing Strategy

My approach to testing the project. \
The biggest challenge is the distributed nature of the microservices architecture. \
In this project, tests are implemented selectively for chosen components to demonstrate each layer of the testing pyramid effectively, not to cover all the possible test scenarios.

### 1. Unit tests
I use them to verify pure business logic. These tests ensure reliability and resilience against invalid input data.
- unit test of MP3 extraction and metadata mapping in resource-processor

### 2. Integration tests
I use them to verify that a single microservice correctly interacts with external infrastructure: database, S3 storage, and RabbitMQ. I verify the correctness of entity mappings without starting neighboring microservices.
- integration test of S3 and RabbitMQ in resource-service
- integration test of database in song-service

### 3. Component tests
I test a single microservice in isolation as an independent component. External services are mocked using WireMock to simulate their HTTP responses. I create test scenarios and define expected behavior using Cucumber.
- component test of isolated business flows with WireMock in resource-service

### 4. Contract tests
They ensure a consistent "language" between microservices across both communication styles: synchronous HTTP and asynchronous messaging. They make sure that messages and requests from one service are fully compatible with expectations in another service, catching format differences without running multiple microservices at the same time.
- synchronous HTTP contract test of song deletion endpoint (`DELETE /songs`) between `resource-service` (consumer) and `song-service` (provider)
- asynchronous messaging contract test for the "resource uploaded" event published to RabbitMQ between `resource-service` (producer) and `resource-processor` (consumer), verified via `@AutoConfigureMessageVerifier` and `StubTrigger`

### 5. End-to-End (E2E) tests
I test the overall system behavior in a fully running environment with databases, storage, and message broker. The scenarios I've previously verified with Postman, I now automate with Cucumber to ensure the entire processing pipeline reacts as expected.
- end-to-end test of audio processing and cascade deletion across all services in dedicated e2e-test module

---

### Approach and combination of strategies
I limit unit tests to verifying pure business logic. For communication between microservices and infrastructure, I rely on integration and contract tests. I use component tests for testing integration with external services. I reserve E2E tests for the most critical paths across the entire system.
