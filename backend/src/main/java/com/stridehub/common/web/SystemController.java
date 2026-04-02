package com.stridehub.common.web;

import com.stridehub.common.api.ApiResponse;
import com.stridehub.common.id.CorrelationIdFilter;
import com.stridehub.common.time.TimeProvider;
import java.time.Instant;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    private final Environment environment;
    private final TimeProvider timeProvider;

    public SystemController(Environment environment, TimeProvider timeProvider) {
        this.environment = environment;
        this.timeProvider = timeProvider;
    }

    @GetMapping("/ping")
    public ApiResponse<SystemPingResponse> ping() {
        Instant now = timeProvider.now();
        return ApiResponse.ok(
                new SystemPingResponse("stridehub", now, List.of(environment.getActiveProfiles())),
                now,
                currentCorrelationId()
        );
    }

    private String currentCorrelationId() {
        String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        return StringUtils.hasText(correlationId) ? correlationId : "n/a";
    }

    public record SystemPingResponse(String service, Instant timestamp, List<String> activeProfiles) {
    }
}
