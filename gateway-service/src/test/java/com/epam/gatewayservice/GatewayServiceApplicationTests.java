package com.epam.gatewayservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "eureka.client.enabled=false")
class GatewayServiceApplicationTests {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    @DisplayName("should configure routes for song-service and resource-service")
    void shouldConfigureRoutes() {
        List<String> routeIds = routeLocator.getRoutes()
                .map(org.springframework.cloud.gateway.route.Route::getId)
                .collectList()
                .block();

        assertThat(routeIds).contains("song-service", "resource-service");
    }
}
