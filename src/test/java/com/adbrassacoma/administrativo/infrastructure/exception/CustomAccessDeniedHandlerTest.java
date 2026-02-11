package com.adbrassacoma.administrativo.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("CustomAccessDeniedHandler")
class CustomAccessDeniedHandlerTest {

    private CustomAccessDeniedHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws IOException {
        handler = new CustomAccessDeniedHandler();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
        when(request.getRequestURI()).thenReturn("/api/auth/usuarios");
        when(request.getMethod()).thenReturn("GET");
    }

    @Test
    void handleDeveRetornar403EJsonComMensagem() throws IOException {
        handler.handle(request, response, new AccessDeniedException("Acesso negado"));

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).getWriter();
        String body = stringWriter.toString();
        assertTrue(body.contains("Acesso negado"));
        assertTrue(body.contains("403"));
        assertTrue(body.contains("/api/auth/usuarios"));
    }
}
