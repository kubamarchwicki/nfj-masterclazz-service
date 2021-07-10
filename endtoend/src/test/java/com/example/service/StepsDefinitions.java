package com.example.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hamcrest.CoreMatchers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.awaitility.Awaitility.await;


public class StepsDefinitions {
    public final String KAFKA_TOPIC = "customer.activity";

    SystemTestEnvironment environment;
    UUID uuid;

    @Before
    public void initialize() {
        this.environment = new SystemTestEnvironment();
        this.uuid = UUID.randomUUID();
    }

    @Given("A GovAudit system integration")
    public void givenGovAuditSystemPresent() {
        environment.wireMock().register(WireMock.post("/api/v2/activity")
                .withHeader("Host", equalTo("important.gov:8080"))
                .willReturn(aResponse()
                        .withStatus(204)
                ));
    }

    @Given("A customer exists")
    public void givenACustomer() {
        environment.wireMock().register(WireMock.get("/public/customer/" + uuid)
                .withHeader("Host", equalTo("customers.svc:8080"))
                .willReturn(aResponse()
                        .withHeader("Content-type", "application/json")
                        .withBody("{" +
                                "\"customerId\": \" " + uuid + " \"" +
                                "}")
                        .withStatus(200)
                ));
    }

    @When("A customer logged-in event is emitted")
    public void customerLoggedInEventEmitted() {
        var time = LocalDateTime.now();

        var payload = "{" +
                "\"customerId\": \"" + uuid + "\"," +
                "\"eventTime\": \"" + time + "\"" +
                "}";

        environment.kafka()
                .send(new ProducerRecord<>(KAFKA_TOPIC, payload));
    }

    @Then("GovAudit system is notified with user data")
    public void govAuditSystemNotified() {
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
