package com.example.service.domain;

import com.example.service.platform.CustomerLoggedInEvent;
import org.springframework.stereotype.Component;

@Component
public class EventsAuditProcessor {

    public void processMessage(CustomerLoggedInEvent event) {
        System.out.println("customerLoggedInEvent = " + event);
    }

}
