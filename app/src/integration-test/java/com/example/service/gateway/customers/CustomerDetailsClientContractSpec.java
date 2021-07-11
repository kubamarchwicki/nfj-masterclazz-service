package com.example.service.gateway.customers;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.example.service.gateway.customers.CustomerDetailsClientContractSpec.PROVIDER_NAME;
import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = PROVIDER_NAME)
class CustomerDetailsClientContractSpec {

    public static final String PROVIDER_NAME = "customers";
    public static final String CONSUMER_NAME = "gov-audit-service";

    private UUID EXISTING_CUSTOMER_ID = UUID.fromString("12345678-0000-0000-0000-000000000000");
    private UUID MISSING_CUSTOMER_ID = UUID.fromString("98765432-0000-0000-0000-000000000000");

    @Pact(provider = PROVIDER_NAME, consumer = CONSUMER_NAME)
    public RequestResponsePact customerExistsPact(PactDslWithProvider builder) {
        return builder
                .given("a customer exists")
                .uponReceiving("get customer details")
                .path("/public/customer/" + EXISTING_CUSTOMER_ID.toString())
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(newJsonBody(o -> {
                    o.uuid("customerId", EXISTING_CUSTOMER_ID);
                    o.object("name", name -> {
                        name.stringType("firstName");
                        name.stringType("lastName");
                    });
                    o.object("email", email -> email.stringType("address", "foo@bar.com"));
                    o.date("dateOfBirth");
                    o.datetime("registrationDate", "yyyy-MM-dd'T'HH:mm:ss'Z'", Instant.parse("2020-10-01T07:46:58.402Z"));
                }).build())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "customerExistsPact")
    void customerExists_returnCustomerDetailDto(MockServer mockServer) {
        Optional<CustomerDetailsDto> maybeCustomer = new CustomerDetailsClient(URI.create(mockServer.getUrl()), new RestTemplate())
                .fetchCustomerById(EXISTING_CUSTOMER_ID);

        assertThat(maybeCustomer)
                .isNotEmpty()
                .hasValueSatisfying(dto -> {
                    assertThat(dto.getCustomerId()).isEqualByComparingTo(EXISTING_CUSTOMER_ID);
                    assertThat(dto.getEmail().getAddress()).isNotEmpty();
                });
    }

    @Pact(provider = PROVIDER_NAME, consumer = CONSUMER_NAME)
    public RequestResponsePact customerMissing(PactDslWithProvider builder) {
        return builder
                .given("a customer does not exists")
                .uponReceiving("get customer details")
                .path("/public/customer/" + MISSING_CUSTOMER_ID)
                .method("GET")
                .willRespondWith()
                .status(404)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "customerMissing")
    void missingCustomer_emptyOptional(MockServer mockServer) {
        Optional<CustomerDetailsDto> maybeCustomer = new CustomerDetailsClient(URI.create(mockServer.getUrl()), new RestTemplate())
                .fetchCustomerById(MISSING_CUSTOMER_ID);

        assertThat(maybeCustomer)
                .isEmpty();
    }
}