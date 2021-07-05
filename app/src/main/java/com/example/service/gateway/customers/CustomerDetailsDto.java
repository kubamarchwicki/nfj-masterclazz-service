package com.example.service.gateway.customers;

import lombok.Value;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

public class CustomerDetailsDto {
    UUID customerId;
    CustomerName name;
    Email email;
    LocalDate dateOfBirth;
    ZonedDateTime registrationDate;

    @Value(staticConstructor = "of")
    public static class CustomerName {
        String firstName;
        String lastName;
    }

    @Value(staticConstructor = "of")
    public static class Email {
        String address;
    }
}
