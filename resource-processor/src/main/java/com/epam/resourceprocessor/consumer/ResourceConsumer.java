package com.epam.resourceprocessor.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.epam.resourceprocessor.service.ResourceProcessingService;

@Component
public class ResourceConsumer {

    private final ResourceProcessingService resourceProcessingService;

    public ResourceConsumer(ResourceProcessingService resourceProcessingService) {
        this.resourceProcessingService = resourceProcessingService;
    }

    @RabbitListener(queues = "${resource.queue}")
    public void processResource(Integer resourceId) {
        resourceProcessingService.process(resourceId);
    }
}
