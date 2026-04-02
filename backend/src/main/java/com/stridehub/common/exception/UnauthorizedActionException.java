package com.stridehub.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends BusinessException {

    public UnauthorizedActionException(String code, String message) {
        super(HttpStatus.FORBIDDEN, code, message);
    }
}
