package com.example.service.platform;

import com.example.service.domain.EventsAuditProcessor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.after;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=localhost:9092")
//@DirtiesContext
//@EmbeddedKafka(partitions = 1, topics = KafkaConsumerConfiguration.CUSTOMER_ACTIVITY_TOPIC,
//        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class GovAuditServiceTCSpec {
    private static final Logger LOGGER = LoggerFactory.getLogger(GovAuditServiceTCSpec.class);

    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    EventsAuditProcessor mockedProcessor;

    @Test
    public void shouldProcessKafkaMessage() throws InterruptedException {
        var uuid = UUID.randomUUID();
        var time = LocalDateTime.now();
        var payload = "{" +
                "\"customerId\": \"" + uuid + "\"," +
                "\"eventTime\": \"" + time + "\"" +
                "}";
        kafkaTemplate.send(KafkaConsumerConfiguration.CUSTOMER_ACTIVITY_TOPIC, payload);

        verify(mockedProcessor, after(5000))
                .processMessage(new CustomerLoggedInEvent(uuid, time));
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}