package com.example.customers;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

@Component
class CustomersRepository {

    public Optional<CustomerDetailsDto> getCustomer(UUID uuid) {
        throw new IllegalStateException("Not implemented");
    }

}

@RestController
@RequiredArgsConstructor
class CustomersController {
    final CustomersRepository repository;

    @GetMapping(path = "/public/customer/{uuid}")
    public ResponseEntity<CustomerDetailsDto> getCustomerDetails(@PathVariable("uuid") UUID uuid) {
        return repository.getCustomer(uuid)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}

@Value
@Builder
class CustomerDetailsDto {
    UUID customerId;
    String additionalId;
    CustomerName name;
    Email email;
    LocalDate dateOfBirth;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
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