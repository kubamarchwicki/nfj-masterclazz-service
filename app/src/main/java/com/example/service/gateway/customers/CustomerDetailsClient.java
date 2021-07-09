package com.example.service.gateway.customers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class CustomerDetailsClient {
    final String customersUri;
    final RestTemplate restTemplate;

    public CustomerDetailsClient(URI customersUri, RestTemplate restTemplate) {
        this.customersUri = customersUri.toString()  + "/public/customer/{uuid}";
        this.restTemplate = restTemplate;
    }

    public Optional<CustomerDetailsDto> fetchCustomerById(UUID uuid) {
        try {
            return Optional.of(restTemplate.getForObject(customersUri, CustomerDetailsDto.class, uuid));
        } catch (HttpClientErrorException e) {
            log.warn("Couldn't resolve client {}", uuid);
            return Optional.empty();
        }
    }

}
