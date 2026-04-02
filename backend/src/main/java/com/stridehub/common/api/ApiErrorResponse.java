package com.stridehub.common.api;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        String code,
        String message,
        List<String> details,
        Instant timestamp,
        String correlationId,
        String path
) {
}
