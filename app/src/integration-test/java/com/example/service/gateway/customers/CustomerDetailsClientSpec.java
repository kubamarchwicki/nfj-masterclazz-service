package com.example.service.gateway.customers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.mkammerer.wiremock.WireMockExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@JsonTest
class CustomerDetailsClientSpec {

    @RegisterExtension
    WireMockExtension wireMock = new WireMockExtension();

    @Autowired
    ObjectMapper mapper;

    @Test
    void customerExists_returnCustomerDetailDto() throws JsonProcessingException {
        var uuid = UUID.randomUUID();

        CustomerDetailsDto dto = CustomerDetailsDto.builder()
                .customerId(uuid)
                .build();

        wireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/public/customer/" + uuid))
                        .willReturn(WireMock.okJson(
                                mapper.writeValueAsString(dto)
                        ))
        );

        Optional<CustomerDetailsDto> maybeCustomer = new CustomerDetailsClient(wireMock.getBaseUri(), new RestTemplate())
                .fetchCustomerById(uuid);

        Assertions.assertThat(maybeCustomer)
                .isNotEmpty()
                .contains(dto);
    }

    @Test
    void missingCustomer_emptyOptional() {
        var uuid = UUID.randomUUID();
        wireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/public/customer/" + uuid))
                        .willReturn(WireMock.notFound())
        );

        Optional<CustomerDetailsDto> maybeCustomer = new CustomerDetailsClient(wireMock.getBaseUri(), new RestTemplate())
                .fetchCustomerById(uuid);

        Assertions.assertThat(maybeCustomer)
                .isEmpty();
    }
}