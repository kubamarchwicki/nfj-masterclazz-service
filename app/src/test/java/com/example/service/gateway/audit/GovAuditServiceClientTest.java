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
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.mockito.Mockito.*;

class GovAuditServiceClientTest {

    @Test
    void shouldRecordTimingInformation_mocks() {
        var registry = mock(MeterRegistry.class);
        var sut = new GovAuditServiceClient(URI.create("http://foo.bar"), mock(RestTemplate.class), registry);

        when(registry.timer("audit.service.timer"))
                .thenReturn(new NoopTimer(mock(Meter.Id.class)));

        sut.dispatchCustomerInformation(GovAuditServiceClient.GovAuditDto.builder().build());

        verify(registry).timer("audit.service.timer");
    }

    @Test
    void shouldRecordTimingInformation() {
        var registry = new SimpleMeterRegistry();

        var sut = new GovAuditServiceClient(URI.create("http://foo.bar"), mock(RestTemplate.class), registry);
        sut.dispatchCustomerInformation(GovAuditServiceClient.GovAuditDto.builder().build());

        Timer timer = registry.timer("audit.service.timer");
        Assertions.assertThat(timer.count())
                .isEqualTo(1);
    }
}