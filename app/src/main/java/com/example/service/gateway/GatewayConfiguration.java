package com.example.service.gateway;

import com.example.service.gateway.audit.GovAuditServiceClient;
import com.example.service.gateway.customers.CustomerDetailsClient;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Configuration
public class GatewayConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    CustomerDetailsClient customerDetailsClient(
            @Value("${app.service.url.customers}") URI customersUri,
            RestTemplate restTemplate) {
        return new CustomerDetailsClient(customersUri, restTemplate);
    }

    @Bean
    GovAuditServiceClient govAuditServiceClient(
            @Value("${gov-endpoint.service.url}") URI govAuditServerUri,
            RestTemplate restTemplate,
            MeterRegistry meterRegistry
    ) {
        return new GovAuditServiceClient(govAuditServerUri, restTemplate, meterRegistry);
    }

}
