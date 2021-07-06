package com.example.service.gateway.audit;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class GovAuditServiceClient {

    final RestTemplate restTemplate;
    final MeterRegistry meterRegistry;

    public ResponseEntity<Void> dispatchCustomerInformation() {
        return meterRegistry.timer("audit.service.timer").record(() -> {
            //TODO: some more processing logic will happen here
            return ResponseEntity.noContent().build();
        });
    }

}
