package com.example.service.platform;

import lombok.Value;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Value
public class CustomerLoggedInEvent {
    public static final String EVENT_NAME = "com.example.events.customer.CustomerLoggedInEvent";

    UUID customerId;
    LocalDateTime eventTime;
}
