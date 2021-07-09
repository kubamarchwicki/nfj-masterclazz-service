package com.example.service.platform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonTest
//@SpringBootTest(classes = JacksonAutoConfiguration.class)
class EventsMappingObjectMapperSpec {

    @Autowired
    ObjectMapper mapper;

    @Test
    void shouldMapObjects() throws JsonProcessingException {
        var uuid = UUID.randomUUID();
        var date = LocalDateTime.parse("2021-02-01T17:44:01.886652");
        var jsonString = "{" +
                "\"customerId\": \"" + uuid + "\"," +
                "\"eventTime\": \"" + date.toString() + "\"" +
                "}";

        var eventFromJson = mapper.readValue(jsonString, CustomerLoggedInEvent.class);

        Assertions.assertThat(eventFromJson)
                .matches(event -> event.getCustomerId().equals(uuid), "Has uuid=" + uuid)
                .matches(event -> event.getEventTime().equals(date), "Has date=" + date);
    }

}