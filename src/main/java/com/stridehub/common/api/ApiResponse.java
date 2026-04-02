package com.stridehub.common.api;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        Instant timestamp,
        String correlationId
) {

    public static <T> ApiResponse<T> ok(T data, Instant timestamp, String correlationId) {
        return new ApiResponse<>(true, data, timestamp, correlationId);
    }
}
