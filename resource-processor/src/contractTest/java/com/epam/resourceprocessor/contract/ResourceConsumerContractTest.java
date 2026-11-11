package com.epam.resourceprocessor.contract;

import com.epam.resourceprocessor.consumer.ResourceConsumer;
import com.epam.resourceprocessor.service.ResourceProcessingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        classes = {
                ResourceConsumer.class,
                ResourceConsumerContractTest.MessagingConfig.class
        },
        properties = "resource.queue=resource-processing-queue"
)
@AutoConfigureStubRunner(
        ids = "com.epam:resource-service:+:stubs",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class ResourceConsumerContractTest {

    @MockitoBean
    private ResourceProcessingService resourceProcessingService;

    @Autowired
    private StubTrigger stubTrigger;

    @Configuration
    static class MessagingConfig {

        @Bean
        public MessageVerifierSender<Message<?>> messageVerifierSender(ResourceConsumer resourceConsumer) {
            return new MessageVerifierSender<Message<?>>() {
                @Override
                public void send(Message<?> message, String destination, YamlContract contract) {
                    send(message.getPayload(), message.getHeaders(), destination, contract);
                }

                @Override
                public <T> void send(T payload, Map<String, Object> headers, String destination, YamlContract contract) {
                    String str = String.valueOf(payload).replace("\"", "").trim();
                    resourceConsumer.processResource(Integer.parseInt(str));
                }
            };
        }

        @Bean
        public MessageVerifierReceiver<Message<?>> messageVerifierReceiver() {
            return new MessageVerifierReceiver<Message<?>>() {
                @Override
                public Message<?> receive(String destination, long timeout, TimeUnit timeUnit, YamlContract contract) {
                    return null;
                }

                @Override
                public Message<?> receive(String destination, YamlContract contract) {
                    return null;
                }
            };
        }
    }

    @Test
    @DisplayName("should receive resource ID from RabbitMQ contract stub and trigger processing")
    void shouldProcessResourceUploaded() {
        // when
        boolean triggered = stubTrigger.trigger("resource_uploaded");

        // then
        assertTrue(triggered);
        verify(resourceProcessingService).process(1);
    }
}
