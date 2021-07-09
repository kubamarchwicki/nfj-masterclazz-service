package com.example.service.gateway.audit;

import com.example.service.gateway.GatewayConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = {GatewayConfiguration.class, SimpleMeterRegistry.class})
class GovAuditServiceClientTimingSpec {

    @Autowired
    MeterRegistry registry;

    @Autowired
    GovAuditServiceClient sut;

    @MockBean
    RestTemplate restTemplate;

    @Test
    void shouldRecordTimingInformation_context() {
        sut.dispatchCustomerInformation(GovAuditServiceClient.GovAuditDto.builder().build());

        Timer timer = registry.timer("audit.service.timer");
        Assertions.assertThat(timer.count())
                .isEqualTo(1);
    }
}