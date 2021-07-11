package com.example.customers;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@Provider("customers")
@PactBroker(url = "https://footest-broker.pactflow.io")
@IgnoreNoPactsToVerify
class CustomerContractVerificationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomersRepository mockRepository;

    @BeforeEach
    void setTarget(PactVerificationContext context) {
        context.setTarget(new MockMvcTestTarget(mockMvc));
    }

    @State("a customer exists")
    void existingCustomer(Map<String, Object> providerStateParameters) {
        when(mockRepository.getCustomer(any()))
                .thenAnswer(invocation -> {
                    UUID uuid = invocation.getArgument(0, UUID.class);
                    return Optional.of(CustomerDetailsDto.builder()
                            .customerId(uuid)
                            .name(CustomerDetailsDto.CustomerName.of(randomAlphabetic(6), randomAlphabetic(12)))
                            .email(CustomerDetailsDto.Email.of("name@domain.org"))
                            .dateOfBirth(LocalDate.now())
                            .registrationDate(ZonedDateTime.now(ZoneOffset.UTC).withSecond(0))
                            .build());
                });

        System.out.println("providerStateParameters = " + providerStateParameters);
    }

    @State("a customer does not exist")
    void noCustomer() {
        when(mockRepository.getCustomer(any()))
            .thenReturn(Optional.empty());
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }


}

