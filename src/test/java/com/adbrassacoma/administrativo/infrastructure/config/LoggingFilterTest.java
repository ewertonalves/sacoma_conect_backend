package com.adbrassacoma.administrativo.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LoggingFilter")
class LoggingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    void doFilterInternalDeveChamarChain() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("User-Agent")).thenReturn("TestAgent");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        LoggingFilter filter = new LoggingFilter();
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalDeveUsarXForwardedForQuandoPresente() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("User-Agent")).thenReturn(null);
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, 10.0.0.2");

        LoggingFilter filter = new LoggingFilter();
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
