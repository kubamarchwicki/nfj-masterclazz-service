package com.example.service.domain;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest
class EventsAuditControllerSpec {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OutboxLogRepository repository;

    @Test
    void emptyLog_shouldReturnNotFound() {
        RestAssuredMockMvc.given()
                .mockMvc(mockMvc)
            .when()
                .get("/audit/outbox-log")
            .then()
                .statusCode(404);
    }

    @Test
    void someLogValues_shouldReturnList() {
        var uuid = UUID.randomUUID();
        when(repository.entries())
                .thenReturn(List.of(new OutboxLogEntry(uuid)));

        RestAssuredMockMvc.given()
                .mockMvc(mockMvc)
            .when()
                .get("/audit/outbox-log")
            .then()
                .body("[0].customerId", CoreMatchers.equalTo(uuid.toString()))
                .statusCode(200);
    }

}
