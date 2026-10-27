# Testing Strategy

My approach to testing the project. The biggest challenge is the distributed nature of the microservices architecture.

### 1. Unit tests
I use them to verify pure business logic. They cover MP3 metadata extraction and field mapping in `resource-processor`. These tests ensure reliability and resilience against invalid input data.

### 2. Integration tests
I use them to verify that a single microservice correctly interacts with external infrastructure: database, S3 storage, and RabbitMQ. I verify the correctness of entity mappings without starting neighboring microservices.

### 3. Component tests
I test a single microservice in isolation as an independent component. External services are mocked using WireMock to simulate their HTTP responses. I create test scenarios and define expected behavior using Cucumber.

### 4. Contract tests
They ensure a consistent "language" between microservices. They make sure that a request from one service is fully compatible with the endpoint requirements in another service. They allow catching format differences without running multiple microservices at the same time.

### 5. End-to-End (E2E) tests
I test the overall system behavior in a fully running environment with databases, storage, and message broker. The scenarios I've previously verified with Postman, I now automate with Cucumber to ensure the entire processing pipeline reacts as expected.

---

### Approach and combination of strategies
I limit unit tests to verifying pure business logic. For communication between microservices and infrastructure, I rely on integration and contract tests. I use component tests for testing integration with external services. I reserve E2E tests for the most critical paths across the entire system.
