package com.example.service.domain;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest
public class LogsTestBase {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OutboxLogRepository repository;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        when(repository.entries())
                .thenReturn(List.of(new OutboxLogEntry(UUID.randomUUID())));
    }
}
