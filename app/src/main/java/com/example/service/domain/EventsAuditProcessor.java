package com.example.service.domain;

import com.example.service.gateway.audit.GovAuditServiceClient;
import com.example.service.gateway.customers.CustomerDetailsClient;
import com.example.service.gateway.customers.CustomerDetailsDto;
import com.example.service.platform.CustomerLoggedInEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
public class EventsAuditProcessor {

    final CustomerDetailsClient customerDetailsClient;
    final GovAuditServiceClient govAuditServiceClient;

    public void processMessage(CustomerLoggedInEvent event) {
        Optional<CustomerDetailsDto> maybeClient = customerDetailsClient.fetchCustomerById(event.getCustomerId());

        maybeClient.map(govAuditServiceClient::buildExternalDto)
                .ifPresent(govAuditServiceClient::dispatchCustomerInformation);
    }

}
