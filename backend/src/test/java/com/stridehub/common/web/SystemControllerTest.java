package com.stridehub.common.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stridehub.common.api.ApiResponse;
import com.stridehub.common.id.CorrelationIdFilter;
import com.stridehub.common.time.TimeProvider;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;

class SystemControllerTest {

    private SystemController controller;
    private Environment environment;
    private TimeProvider timeProvider;
    private Instant fixedTime;

    @BeforeEach
    void setUp() {
        environment = mock(Environment.class);
        timeProvider = mock(TimeProvider.class);
        fixedTime = Instant.parse("2024-01-01T12:00:00Z");
        when(timeProvider.now()).thenReturn(fixedTime);

        controller = new SystemController(environment, timeProvider);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldReturnPingResponseWithServiceName() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});

        ApiResponse<SystemController.SystemPingResponse> response = controller.ping();

        assertThat(response.success()).isTrue();
        assertThat(response.data().service()).isEqualTo("stridehub");
        assertThat(response.data().timestamp()).isEqualTo(fixedTime);
        assertThat(response.data().activeProfiles()).containsExactly("test");
    }

    @Test
    void shouldReturnMultipleActiveProfiles() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"test", "local"});

        ApiResponse<SystemController.SystemPingResponse> response = controller.ping();

        assertThat(response.data().activeProfiles()).containsExactly("test", "local");
    }

    @Test
    void shouldIncludeCorrelationIdFromMDC() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});
        MDC.put(CorrelationIdFilter.MDC_KEY, "test-correlation-id");

        ApiResponse<SystemController.SystemPingResponse> response = controller.ping();

        assertThat(response.correlationId()).isEqualTo("test-correlation-id");
    }

    @Test
    void shouldReturnNotAvailableWhenNoCorrelationId() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});

        ApiResponse<SystemController.SystemPingResponse> response = controller.ping();

        assertThat(response.correlationId()).isEqualTo("n/a");
    }

    @Test
    void shouldUseCurrentTimestamp() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});

        ApiResponse<SystemController.SystemPingResponse> response = controller.ping();

        assertThat(response.timestamp()).isEqualTo(fixedTime);
        assertThat(response.data().timestamp()).isEqualTo(fixedTime);
    }
}
