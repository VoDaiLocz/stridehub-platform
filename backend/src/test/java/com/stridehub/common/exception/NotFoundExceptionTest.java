package com.stridehub.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class NotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithCodeAndMessage() {
        String code = "user_not_found";
        String message = "User with id 123 not found";

        NotFoundException exception = new NotFoundException(code, message);

        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldBeThrowable() {
        NotFoundException exception = new NotFoundException("test_code", "Test message");

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception).isInstanceOf(BusinessException.class);
    }
}
