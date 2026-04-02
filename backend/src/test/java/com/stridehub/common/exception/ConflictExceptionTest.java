package com.stridehub.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ConflictExceptionTest {

    @Test
    void shouldCreateExceptionWithCodeAndMessage() {
        String code = "duplicate_email";
        String message = "User with email already exists";

        ConflictException exception = new ConflictException(code, message);

        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldBeThrowable() {
        ConflictException exception = new ConflictException("test_code", "Test message");

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception).isInstanceOf(BusinessException.class);
    }
}
