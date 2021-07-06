package com.example.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
class EventsAuditController {

    final OutboxLogRepository repository;

    @GetMapping("/audit/outbox-log")
    public ResponseEntity<List<OutboxLogEntry>> getLastOutboxEntries() {
        List<OutboxLogEntry> logs = repository.entries();

        if (logs.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(logs);
        }
    }

}

@Component
class OutboxLogRepository {

    public List<OutboxLogEntry> entries() {
        throw new IllegalStateException("Not implemented");
    }

}
