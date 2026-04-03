package com.stridehub.common.id;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CorrelationIdAccessor {

    public String currentOrDefault() {
        String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        return StringUtils.hasText(correlationId) ? correlationId : "n/a";
    }
}
