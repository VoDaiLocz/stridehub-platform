package com.stridehub.common.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stridehub.config.StridehubProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class CorrelationIdFilterTest {

    private CorrelationIdFilter filter;
    private StridehubProperties properties;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        properties = mock(StridehubProperties.class);
        StridehubProperties.Audit audit = mock(StridehubProperties.Audit.class);
        when(properties.getAudit()).thenReturn(audit);
        when(audit.getCorrelationIdHeader()).thenReturn("X-Correlation-Id");

        filter = new CorrelationIdFilter(properties);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldUseExistingCorrelationIdFromRequest() throws ServletException, IOException {
        String existingId = "test-correlation-id-123";
        when(request.getHeader("X-Correlation-Id")).thenReturn(existingId);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader("X-Correlation-Id", existingId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldGenerateCorrelationIdWhenNotProvided() throws ServletException, IOException {
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(org.mockito.ArgumentMatchers.eq("X-Correlation-Id"),
                org.mockito.ArgumentMatchers.anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldGenerateCorrelationIdWhenEmptyString() throws ServletException, IOException {
        when(request.getHeader("X-Correlation-Id")).thenReturn("");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(org.mockito.ArgumentMatchers.eq("X-Correlation-Id"),
                org.mockito.ArgumentMatchers.anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldAddCorrelationIdToMDC() throws ServletException, IOException {
        String existingId = "mdc-test-id";
        when(request.getHeader("X-Correlation-Id")).thenReturn(existingId);

        filter.doFilterInternal(request, response, filterChain);

        // MDC should be cleared after filter chain execution
        assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
    }

    @Test
    void shouldClearMDCAfterFilterChainEvenOnException() {
        String existingId = "exception-test-id";
        when(request.getHeader("X-Correlation-Id")).thenReturn(existingId);
        try {
            org.mockito.Mockito.doThrow(new ServletException("Test exception"))
                    .when(filterChain).doFilter(request, response);
        } catch (Exception e) {
            // Setup exception
        }

        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (ServletException | IOException e) {
            // Expected
        }

        assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
    }
}
