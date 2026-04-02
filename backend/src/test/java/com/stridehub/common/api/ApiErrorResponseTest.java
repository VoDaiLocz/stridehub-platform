package com.stridehub.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ApiErrorResponseTest {

    @Test
    void shouldCreateErrorResponseWithAllFields() {
        String code = "validation_error";
        String message = "Validation failed";
        List<String> details = List.of("field1: error1", "field2: error2");
        Instant timestamp = Instant.parse("2024-01-01T12:00:00Z");
        String correlationId = "test-correlation-id";
        String path = "/api/v1/test";

        ApiErrorResponse response = new ApiErrorResponse(
                code, message, details, timestamp, correlationId, path
        );

        assertThat(response.code()).isEqualTo(code);
        assertThat(response.message()).isEqualTo(message);
        assertThat(response.details()).hasSize(2);
        assertThat(response.details()).containsExactly("field1: error1", "field2: error2");
        assertThat(response.timestamp()).isEqualTo(timestamp);
        assertThat(response.correlationId()).isEqualTo(correlationId);
        assertThat(response.path()).isEqualTo(path);
    }

    @Test
    void shouldHandleEmptyDetails() {
        ApiErrorResponse response = new ApiErrorResponse(
                "error_code",
                "Error message",
                List.of(),
                Instant.now(),
                "correlation-id",
                "/path"
        );

        assertThat(response.details()).isEmpty();
    }

    @Test
    void shouldPreserveAllFieldsInRecord() {
        Instant timestamp = Instant.parse("2024-06-15T10:30:00Z");
        ApiErrorResponse response = new ApiErrorResponse(
                "not_found",
                "Resource not found",
                List.of("User with id 123 not found"),
                timestamp,
                "abc-123",
                "/api/v1/users/123"
        );

        assertThat(response.code()).isEqualTo("not_found");
        assertThat(response.message()).isEqualTo("Resource not found");
        assertThat(response.timestamp()).isEqualTo(timestamp);
        assertThat(response.correlationId()).isEqualTo("abc-123");
        assertThat(response.path()).isEqualTo("/api/v1/users/123");
    }
}
