package com.stridehub.common.exception;

import com.stridehub.common.api.ApiErrorResponse;
import com.stridehub.common.id.CorrelationIdFilter;
import com.stridehub.common.time.TimeProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final TimeProvider timeProvider;

    public GlobalExceptionHandler(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(exception.getStatus())
                .body(error(
                        exception.getCode(),
                        exception.getMessage(),
                        List.of(),
                        request
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> details = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .toList();

        return ResponseEntity.badRequest()
                .body(error("validation_error", "Request validation failed", details, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error(
                        "internal_error",
                        "An unexpected error occurred",
                        List.of(),
                        request
                ));
    }

    private ApiErrorResponse error(
            String code,
            String message,
            List<String> details,
            HttpServletRequest request
    ) {
        return new ApiErrorResponse(
                code,
                message,
                details,
                timeProvider.now(),
                currentCorrelationId(request),
                request.getRequestURI()
        );
    }

    private String currentCorrelationId(HttpServletRequest request) {
        String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        if (!StringUtils.hasText(correlationId)) {
            correlationId = request.getHeader(CorrelationIdFilter.HEADER_NAME);
        }
        return StringUtils.hasText(correlationId) ? correlationId : "n/a";
    }
}
