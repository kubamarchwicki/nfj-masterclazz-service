package com.example.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.awaitility.Awaitility.await;

@Slf4j
class GovAuditingIntegrationTest {

    @RegisterExtension
    public static SystemTestEnvironment environment = new SystemTestEnvironment();
    public final String KAFKA_TOPIC = "customer.activity";

    @Test
    void customerLoggedIn_notifyGovAuditSystem() throws Exception {
        var uuid = UUID.randomUUID();
        var time = LocalDateTime.now();

        environment.wireMock().register(WireMock.get("/public/customer/" + uuid)
                .withHeader("Host", equalTo("customers.svc:8080"))
                .willReturn(aResponse()
                        .withHeader("Content-type", "application/json")
                        .withBody("{" +
                                "\"customerId\": \" "+ uuid +" \"" +
                                "}")
                        .withStatus(200)
                ));

        environment.wireMock().register(WireMock.post("/api/v2/activity")
                .withHeader("Host", equalTo("important.gov:8080"))
                .willReturn(aResponse()
                        .withStatus(204)
                ));

        var payload = "{" +
                "\"customerId\": \"" + uuid + "\"," +
                "\"eventTime\": \"" + time + "\"" +
                "}";

        environment.kafka()
                .send(new ProducerRecord<>(KAFKA_TOPIC, payload));

        var request = postRequestedFor(urlEqualTo("/api/v2/activity"))
                .withHeader("Host", equalTo("important.gov:8080"))
                .withRequestBody(matchingJsonPath("$.customer_id", equalTo(uuid.toString())));
        try {
            await().atMost(Duration.ofSeconds(5))
                    .pollInterval(Duration.ofMillis(500))
                    .until(() -> environment.wireMock().find(request).size(), CoreMatchers.equalTo(1));
        } finally {
            environment.wireMock().verifyThat(1, request);
        }
    }

}
