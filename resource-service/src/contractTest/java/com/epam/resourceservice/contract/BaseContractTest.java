package com.epam.resourceservice.contract;

import com.epam.resourceservice.client.SongServiceClient;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.ResourceService;
import com.epam.resourceservice.service.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = {
                ResourceService.class,
                BaseContractTest.MessagingConfig.class
        },
        properties = "resource.queue=resource-processing-queue"
)
@AutoConfigureMessageVerifier
public abstract class BaseContractTest {

    private static final BlockingQueue<String> capturedMessages = new LinkedBlockingQueue<>();

    @MockitoBean
    private ResourceRepository resourceRepository;

    @MockitoBean
    private S3StorageService s3StorageService;

    @MockitoBean
    private SongServiceClient songServiceClient;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ResourceService resourceService;

    @Configuration
    static class MessagingConfig {

        @Bean
        public MessageVerifierReceiver<Message<?>> messageVerifierReceiver() {
            return new MessageVerifierReceiver<Message<?>>() {
                @Override
                public Message<?> receive(String destination, long timeout, TimeUnit timeUnit, YamlContract contract) {
                    try {
                        String payload = capturedMessages.poll(timeout, timeUnit);
                        return payload != null ? MessageBuilder.withPayload(payload).build() : null;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }

                @Override
                public Message<?> receive(String destination, YamlContract contract) {
                    return receive(destination, 5, TimeUnit.SECONDS, contract);
                }
            };
        }
    }

    @BeforeEach
    public void setup() {
        capturedMessages.clear();

        Resource mockResource = mock(Resource.class);
        when(mockResource.getId()).thenReturn(1);
        when(resourceRepository.save(any(Resource.class))).thenReturn(mockResource);

        doAnswer(invocation -> {
            Object payload = invocation.getArgument(1);
            capturedMessages.offer(String.valueOf(payload));
            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), any(Object.class));
    }

    public void triggerResourceUploaded() {
        resourceService.createResource("sample audio content".getBytes());
    }
}
