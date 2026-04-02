package com.stridehub.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stridehub.common.api.ApiErrorResponse;
import com.stridehub.common.time.TimeProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private TimeProvider timeProvider;
    private HttpServletRequest request;
    private Instant fixedTime;

    @BeforeEach
    void setUp() {
        timeProvider = mock(TimeProvider.class);
        fixedTime = Instant.parse("2024-01-01T12:00:00Z");
        when(timeProvider.now()).thenReturn(fixedTime);

        handler = new GlobalExceptionHandler(timeProvider);
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    void shouldHandleNotFoundException() {
        NotFoundException exception = new NotFoundException("not_found", "Resource not found");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("not_found");
        assertThat(response.getBody().message()).isEqualTo("Resource not found");
        assertThat(response.getBody().timestamp()).isEqualTo(fixedTime);
        assertThat(response.getBody().path()).isEqualTo("/api/v1/test");
        assertThat(response.getBody().details()).isEmpty();
    }

    @Test
    void shouldHandleConflictException() {
        ConflictException exception = new ConflictException("duplicate", "Resource already exists");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("duplicate");
        assertThat(response.getBody().message()).isEqualTo("Resource already exists");
    }

    @Test
    void shouldHandleUnauthorizedActionException() {
        UnauthorizedActionException exception = new UnauthorizedActionException(
                "forbidden",
                "Access denied"
        );

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("forbidden");
        assertThat(response.getBody().message()).isEqualTo("Access denied");
    }

    @Test
    void shouldHandleValidationException() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "test");
        bindingResult.addError(new FieldError("test", "email", "must be valid"));
        bindingResult.addError(new FieldError("test", "name", "must not be blank"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("validation_error");
        assertThat(response.getBody().message()).isEqualTo("Request validation failed");
        assertThat(response.getBody().details()).hasSize(2);
        assertThat(response.getBody().details()).contains("email: must be valid", "name: must not be blank");
    }

    @Test
    void shouldHandleUnexpectedException() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ApiErrorResponse> response = handler.handleUnexpectedException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("internal_error");
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().details()).isEmpty();
    }

    @Test
    void shouldIncludeCorrelationIdInErrorResponse() {
        when(request.getHeader("X-Correlation-Id")).thenReturn("test-correlation-123");
        NotFoundException exception = new NotFoundException("not_found", "Test");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(exception, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().correlationId()).isNotNull();
    }
}
