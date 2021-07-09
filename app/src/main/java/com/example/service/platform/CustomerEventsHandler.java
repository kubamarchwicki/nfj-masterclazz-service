package com.example.service.platform;

import com.example.service.domain.EventsAuditProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;
import java.io.UncheckedIOException;

@RequiredArgsConstructor
class CustomerEventsHandler {

    final ObjectMapper objectMapper;
    final EventsAuditProcessor processor;

    @KafkaListener(topics = KafkaConsumerConfiguration.CUSTOMER_ACTIVITY_TOPIC)
    public void listen(ConsumerRecord<?, String> record) {
        try {
            CustomerLoggedInEvent customerLoggedInEvent = objectMapper.readValue(record.value(), CustomerLoggedInEvent.class);
            processor.processMessage(customerLoggedInEvent);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format(
                    "Unable to deserialize event. Type: '%s', Record: '%s'", CustomerLoggedInEvent.class, record), e);
        }
    }

}
