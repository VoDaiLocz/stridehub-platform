package com.stridehub.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void shouldCreateSuccessResponseWithOkFactory() {
        Instant timestamp = Instant.parse("2024-01-01T12:00:00Z");
        String correlationId = "test-correlation-id";
        String data = "test data";

        ApiResponse<String> response = ApiResponse.ok(data, timestamp, correlationId);

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isEqualTo(data);
        assertThat(response.timestamp()).isEqualTo(timestamp);
        assertThat(response.correlationId()).isEqualTo(correlationId);
    }

    @Test
    void shouldHandleNullData() {
        Instant timestamp = Instant.now();
        String correlationId = "test-id";

        ApiResponse<String> response = ApiResponse.ok(null, timestamp, correlationId);

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldWorkWithComplexDataTypes() {
        record TestData(String name, int value) {}
        TestData data = new TestData("test", 42);
        Instant timestamp = Instant.now();

        ApiResponse<TestData> response = ApiResponse.ok(data, timestamp, "correlation-123");

        assertThat(response.success()).isTrue();
        assertThat(response.data().name()).isEqualTo("test");
        assertThat(response.data().value()).isEqualTo(42);
    }
}
