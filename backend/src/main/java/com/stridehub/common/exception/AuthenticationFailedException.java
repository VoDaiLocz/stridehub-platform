package com.stridehub.common.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends BusinessException {

    public AuthenticationFailedException(String code, String message) {
        super(HttpStatus.UNAUTHORIZED, code, message);
    }
}
