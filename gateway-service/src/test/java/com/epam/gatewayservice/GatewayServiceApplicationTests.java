package com.epam.gatewayservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "eureka.client.enabled=false")
class GatewayServiceApplicationTests {

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("should configure routes for song-service and resource-service")
    void shouldConfigureRoutes() {
        List<String> routeIds = routeLocator.getRoutes()
                .map(org.springframework.cloud.gateway.route.Route::getId)
                .collectList()
                .block();

        assertThat(routeIds).contains("song-service", "resource-service");
    }

    @Test
    @DisplayName("should return 404 with JSON error response for undefined route")
    void shouldReturn404ForUndefinedRoute() {
        webTestClient.get()
                .uri("/unknown-endpoint")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("404")
                .jsonPath("$.errorMessage").isEqualTo("Path not found: /unknown-endpoint");
    }

    @Test
    @DisplayName("should return 503 with JSON error response when target service is unavailable")
    void shouldReturn503WhenTargetServiceUnavailable() {
        webTestClient.get()
                .uri("/songs/1")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("503")
                .jsonPath("$.errorMessage").value(msg ->
                        assertThat((String) msg).contains("Service is unavailable"));
    }
}
