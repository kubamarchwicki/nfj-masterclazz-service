package com.example.service.gateway.audit;

import com.example.service.gateway.customers.CustomerDetailsClient;
import com.example.service.gateway.customers.CustomerDetailsDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.mkammerer.wiremock.WireMockExtension;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@JsonTest
class GovAuditServiceClientSpec {

    @RegisterExtension
    WireMockExtension wireMock = new WireMockExtension();

    @Test
    void customerData_notifyGovAudit() throws JsonProcessingException {
        var uuid = UUID.randomUUID();
        GovAuditServiceClient.GovAuditDto dto = GovAuditServiceClient.GovAuditDto.builder()
                .customerId(uuid.toString())
                .build();

        wireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo("/api/v2/activity"))
                        .willReturn(WireMock.created())
        );

        GovAuditServiceClient sut = new GovAuditServiceClient(wireMock.getBaseUri(), new RestTemplate(), new SimpleMeterRegistry());
        sut.dispatchCustomerInformation(dto);

        wireMock.verify(postRequestedFor(urlEqualTo("/api/v2/activity"))
                .withRequestBody(matchingJsonPath("$.customer_id", equalTo(uuid.toString()))));
    }
}