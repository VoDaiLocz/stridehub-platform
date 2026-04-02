package com.stridehub.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UnauthorizedActionExceptionTest {

    @Test
    void shouldCreateExceptionWithCodeAndMessage() {
        String code = "insufficient_permissions";
        String message = "User does not have permission to perform this action";

        UnauthorizedActionException exception = new UnauthorizedActionException(code, message);

        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldBeThrowable() {
        UnauthorizedActionException exception = new UnauthorizedActionException(
                "test_code",
                "Test message"
        );

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception).isInstanceOf(BusinessException.class);
    }
}
