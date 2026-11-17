package com.epam.resourceservice.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.epam.resourceservice.service.ResourceService;

@Component
public class ResourceProcessedConsumer {

    private final ResourceService resourceService;

    public ResourceProcessedConsumer(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @RabbitListener(queues = "${resource.processed.queue}")
    public void onResourceProcessed(Integer resourceId) {
        resourceService.promoteToPermanent(resourceId);
    }
}
