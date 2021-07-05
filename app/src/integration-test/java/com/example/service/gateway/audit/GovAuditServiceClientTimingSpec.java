package com.example.service.gateway.audit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {GovAuditServiceClient.class, SimpleMeterRegistry.class})
class GovAuditServiceClientTimingSpec {

    @Autowired
    MeterRegistry registry;

    @Autowired
    GovAuditServiceClient sut;

    @Test
    void shouldRecordTimingInformation_context() {
        sut.dispatchCustomerInformation();

        Timer timer = registry.timer("audit.service.timer");
        Assertions.assertThat(timer.count())
                .isEqualTo(1);
    }
}