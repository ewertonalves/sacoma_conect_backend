package com.adbrassacoma.administrativo.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitFilter")
class RateLimitFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() throws Exception {
        filter = new RateLimitFilter();
        ReflectionTestUtils.setField(filter, "generalRateLimit", 100);
        ReflectionTestUtils.setField(filter, "authRateLimit", 5);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
    }

    @Test
    void doFilterInternalDeveChamarChainParaEndpointActuator() throws Exception {
        when(request.getRequestURI()).thenReturn("/actuator/health");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }

    @Test
    void doFilterInternalDeveChamarChainParaEndpointApiQuandoDentroDoLimite() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/membros");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response).setHeader("X-Rate-Limit-Limit", "100");
        verify(response, never()).setStatus(429);
    }

    @Test
    void doFilterInternalDeveRetornar429QuandoLimiteExcedido() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        for (int i = 0; i < 6; i++) {
            filter.doFilterInternal(request, response, filterChain);
        }
        verify(response, atLeastOnce()).setStatus(429);
    }
}
