package com.epam.resourceprocessor.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.epam.resourceprocessor.client.ResourceServiceClient;

@Component
public class ResourceConsumer {

    private final ResourceServiceClient resourceServiceClient;

    public ResourceConsumer(ResourceServiceClient resourceServiceClient) {
        this.resourceServiceClient = resourceServiceClient;
    }

    @RabbitListener(queues = "${resource.queue}")
    public void processResource(Integer resourceId) {
        byte[] resource = resourceServiceClient.getResource(resourceId);
        System.out.println(String.format("[%d]: length %d", resourceId, resource.length));
    }
}
