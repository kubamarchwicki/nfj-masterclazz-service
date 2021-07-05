package com.example.service.gateway.audit;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.noop.NoopTimer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

class GovAuditServiceClientTest {

    @Test
    void shouldRecordTimingInformation_mocks() {
        var registry = Mockito.mock(MeterRegistry.class);
        var sut = new GovAuditServiceClient(registry);

        Mockito.when(registry.timer("audit.service.timer"))
                .thenReturn(new NoopTimer(Mockito.mock(Meter.Id.class)));

        sut.dispatchCustomerInformation();

        Mockito.verify(registry).timer("audit.service.timer");
    }

    @Test
    void shouldRecordTimingInformation() {
        var registry = new SimpleMeterRegistry();

        var sut = new GovAuditServiceClient(registry);
        sut.dispatchCustomerInformation();

        Timer timer = registry.timer("audit.service.timer");
        Assertions.assertThat(timer.count())
                .isEqualTo(1);
    }
}