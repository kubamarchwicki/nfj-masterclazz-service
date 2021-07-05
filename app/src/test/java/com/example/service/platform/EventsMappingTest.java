package com.example.service.platform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

class EventsMappingTest {


//    WRONG mapping configuration

    @Test @Disabled("Invalid test - won't pass")
    void shouldMapToJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CustomerLoggedInEvent event = new CustomerLoggedInEvent(
                UUID.randomUUID(),
                LocalDateTime.now()
        );

        var jsonString = mapper.writeValueAsString(event);
        var eventFromJson = mapper.readValue(jsonString, CustomerLoggedInEvent.class);

        Assertions.assertThat(eventFromJson)
                .isNotSameAs(event)
                .isEqualTo(event);
    }







//    Different Configuration than Application

    @Test
    void mapperWithConfiguration() throws JsonProcessingException {
        ObjectMapper mapper =
                new ObjectMapper()
                        .registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat());

        CustomerLoggedInEvent event = new CustomerLoggedInEvent(
                UUID.randomUUID(),
                LocalDateTime.now()
        );

        var jsonString = mapper.writeValueAsString(event);
        var eventFromJson = mapper.readValue(jsonString, CustomerLoggedInEvent.class);

        Assertions.assertThat(eventFromJson)
                .isNotSameAs(event)
                .isEqualTo(event);
    }





//    Correct to and from JSON verification

    @Test
    void parseJsonStringToObject() throws JsonProcessingException {
        ObjectMapper mapper =
                new ObjectMapper()
                        .registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat());

        var uuid = UUID.randomUUID();
        var date = LocalDateTime.parse("2021-02-01T17:44:01.886652");
        var jsonString = "{" +
                "\"customerId\": \"" + uuid + "\"," +
                "\"eventTime\": \"" + date.toString() + "\"" +
                "}";
        var eventFromJson = mapper.readValue(jsonString, CustomerLoggedInEvent.class);

        Assertions.assertThat(eventFromJson)
                .matches(event -> event.getCustomerId().equals(uuid), "Has same uuid")
                .matches(event -> event.getEventTime().equals(date), "Has same dates");
    }


    @Test
    void parseObjectToJsonString() throws JsonProcessingException {
        ObjectMapper mapper =
                new ObjectMapper()
                        .registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat());

        CustomerLoggedInEvent event = new CustomerLoggedInEvent(
                UUID.randomUUID(),
                LocalDateTime.now()
        );
        var jsonString = mapper.writeValueAsString(event);

        Assertions.assertThat(jsonString)
                .contains(
                        "\"customerId\":\"" + event.getCustomerId() + "\"",
                        "\"eventTime\":\"" + event.getEventTime().toLocalDate()
                );
    }

}