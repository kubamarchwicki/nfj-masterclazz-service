package com.example.service.domain;

import lombok.Value;

import java.util.UUID;

@Value
public class OutboxLogEntry {

    UUID customerId;

}
