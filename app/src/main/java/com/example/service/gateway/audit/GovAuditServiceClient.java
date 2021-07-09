package com.example.service.gateway.audit;

import com.example.service.gateway.customers.CustomerDetailsDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class GovAuditServiceClient {

    final RestTemplate restTemplate;
    final MeterRegistry meterRegistry;
    final String govAuditServerUri;

    public GovAuditServiceClient(URI govAuditServerUri, RestTemplate restTemplate, MeterRegistry meterRegistry) {
        this.govAuditServerUri = govAuditServerUri.toString()  + "/api/v2/activity";
        this.restTemplate = restTemplate;
        this.meterRegistry = meterRegistry;
    }

    public ResponseEntity<Void> dispatchCustomerInformation(GovAuditDto dto) {
        return meterRegistry.timer("audit.service.timer").record(
                () -> restTemplate.postForEntity(govAuditServerUri, dto, Void.class)
        );
    }

    public GovAuditDto buildExternalDto(CustomerDetailsDto customerDetailsDto) {
        return GovAuditDto.builder()
                .customerId(customerDetailsDto.getCustomerId().toString())
                .build();
    }

    @Value
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class GovAuditDto {
        String customerId;
    }
}
