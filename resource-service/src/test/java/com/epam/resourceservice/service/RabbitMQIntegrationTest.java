package com.epam.resourceservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.epam.resourceservice.config.RabbitMQConfig;

@SpringBootTest(classes = RabbitMQConfig.class)
@ImportAutoConfiguration(RabbitAutoConfiguration.class)
@Testcontainers
class RabbitMQIntegrationTest {
    private static final long TIMEOUT = 5000L;

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:3-management-alpine"));

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${resource.queue}")
    private String queueName;

    @Test
    @DisplayName("Should send message to queue and receive it successfully")
    void shouldSendAndReceiveMessage() {
        // given
        String messagePayload = "123";

        // when
        rabbitTemplate.convertAndSend(queueName, messagePayload);
        Object received = rabbitTemplate.receiveAndConvert(queueName, TIMEOUT);

        // then
        assertThat(received).isNotNull();
        assertThat(received).isEqualTo(messagePayload);
    }
}
