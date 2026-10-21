package com.epam.resourceprocessor.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResourceConsumer {
    @RabbitListener(queues = "${resource.queue}")
    public void processResource(Integer resourceId) {
        System.out.println("Processing resource: " + resourceId);
    }
}
