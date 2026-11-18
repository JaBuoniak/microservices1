package com.epam.resourceservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${resource.queue}")
    private String queueName;

    @Value("${resource.processed.queue}")
    private String processedQueueName;

    @Bean
    public Queue resourceProcessingQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue resourceProcessedQueue() {
        return new Queue(processedQueueName, true);
    }
}
