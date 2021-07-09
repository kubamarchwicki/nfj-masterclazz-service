package com.example.service.gateway.customers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.mkammerer.wiremock.WireMockExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@JsonTest
@Testcontainers
class CustomerDetailsClientTCSpec {

    @Container
    static GenericContainer container = new GenericContainer(DockerImageName.parse("rodolpheche/wiremock"))
            .withExposedPorts(8080);

    @Autowired
    ObjectMapper mapper;

    WireMock wireMock;
    URI uri;

    @BeforeEach
    void setup() {
        wireMock = WireMock.create()
                .host(container.getContainerIpAddress())
                .port(container.getMappedPort(8080)).build();
        uri = URI.create("http://" + container.getContainerIpAddress() + ":" + container.getMappedPort(8080));
    }

    @Test
    void customerExists_returnCustomerDetailDto() throws JsonProcessingException {
        var uuid = UUID.randomUUID();

        CustomerDetailsDto dto = CustomerDetailsDto.builder()
                .customerId(uuid)
                .build();

        wireMock.register(
                WireMock.get(WireMock.urlEqualTo("/public/customer/" + uuid))
                        .willReturn(WireMock.okJson(
                                mapper.writeValueAsString(dto)
                        ))
        );

        Optional<CustomerDetailsDto> maybeCustomer = new CustomerDetailsClient(uri, new RestTemplate())
                .fetchCustomerById(uuid);

        Assertions.assertThat(maybeCustomer)
                .isNotEmpty()
                .contains(dto);
    }

    @Test
    void missingCustomer_emptyOptional() {
        var uuid = UUID.randomUUID();
        wireMock.register(
                WireMock.get(WireMock.urlEqualTo("/public/customer/" + uuid))
                        .willReturn(WireMock.notFound())
        );

        Optional<CustomerDetailsDto> maybeCustomer = new CustomerDetailsClient(uri, new RestTemplate())
                .fetchCustomerById(uuid);

        Assertions.assertThat(maybeCustomer)
                .isEmpty();
    }
}