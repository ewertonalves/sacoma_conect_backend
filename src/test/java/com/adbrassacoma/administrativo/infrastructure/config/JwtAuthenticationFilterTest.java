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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Test
    void shouldNotFilterQuandoPathSwagger() {
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");
        when(request.getMethod()).thenReturn("GET");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilterQuandoPathLogin() {
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(request.getMethod()).thenReturn("POST");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilterQuandoMethodOptions() {
        when(request.getRequestURI()).thenReturn("/api/membros");
        when(request.getMethod()).thenReturn("OPTIONS");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldFilterQuandoPathProtegido() {
        when(request.getRequestURI()).thenReturn("/api/membros");
        when(request.getMethod()).thenReturn("GET");
        assertFalse(filter.shouldNotFilter(request));
    }

    @Test
    void doFilterInternalDeveChamarChainQuandoSemHeaderAuthorization() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).validateToken(anyString());
    }

    @Test
    void doFilterInternalDeveChamarChainQuandoAuthorizationNaoBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic xxx");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).validateToken(anyString());
    }

    @Test
    void doFilterInternalDeveChamarChainQuandoTokenInvalido() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
        when(jwtService.validateToken("token-invalido")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService).validateToken("token-invalido");
    }

    @Test
    void doFilterInternalDeveAutenticarQuandoTokenValido() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtService.validateToken("token-valido")).thenReturn(true);
        when(jwtService.extractUsername("token-valido")).thenReturn("user@test.com");
        User user = new User("user@test.com", "senha", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(user);
        when(jwtService.validateToken("token-valido", user)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalDeveRetornar401QuandoExcecaoAoProcessarToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-quebrado");
        when(jwtService.validateToken("token-quebrado")).thenThrow(new RuntimeException("Token inválido"));
        when(response.getWriter()).thenReturn(new java.io.PrintWriter(new java.io.StringWriter()));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
    }
}
