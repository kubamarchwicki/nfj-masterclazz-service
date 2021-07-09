package com.example.service.domain;

import com.example.service.gateway.audit.GovAuditServiceClient;
import com.example.service.gateway.customers.CustomerDetailsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DomainConfiguration {

    @Bean
    EventsAuditProcessor eventsAuditProcessor(CustomerDetailsClient customerDetailsClient,
                                              GovAuditServiceClient govAuditServiceClient) {
        return new EventsAuditProcessor(customerDetailsClient, govAuditServiceClient);
    }
}
