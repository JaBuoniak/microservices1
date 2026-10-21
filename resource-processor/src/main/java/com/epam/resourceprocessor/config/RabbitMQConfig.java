package com.epam.resourceprocessor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

@Configuration
public class RabbitMQConfig {

    @Value("${resource.queue}")
    private String queueName;

    @Bean
    public Queue resourceProcessingQueue() {
        return new Queue(queueName, true);
    }
}
