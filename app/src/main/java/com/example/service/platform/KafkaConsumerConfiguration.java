package com.example.service.platform;

import com.example.service.domain.EventsAuditProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
class KafkaConsumerConfiguration {
    public static final String CUSTOMER_ACTIVITY_TOPIC = "customer.activity";

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(CUSTOMER_ACTIVITY_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    CustomerEventsHandler customerEventsHandler(ObjectMapper mapper,
                                                EventsAuditProcessor processor) {
        return new CustomerEventsHandler(mapper, processor);
    }
}
