package com.adbrassacoma.administrativo.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtService")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "MinhaChaveSecretaSuperSeguraParaJWTTokenComPeloMenos256BitsDeTamanhoParaSeguranca");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
    }

    @Test
    void deveGerarTokenParaUserDetails() {
        UserDetails userDetails = User.builder()
                .username("teste@email.com")
                .password("senha")
                .authorities(Collections.emptyList())
                .build();

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void deveGerarTokenParaEmail() {
        String token = jwtService.generateToken("usuario@teste.com");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void deveExtrairUsernameDoToken() {
        String email = "extrair@teste.com";
        String token = jwtService.generateToken(email);
        String extracted = jwtService.extractUsername(token);
        assertEquals(email, extracted);
    }

    @Test
    void validateTokenComUserDetailsDeveRetornarTrueQuandoValido() {
        UserDetails userDetails = User.builder()
                .username("valido@email.com")
                .password("senha")
                .authorities(Collections.emptyList())
                .build();
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateTokenComUserDetailsDeveRetornarFalseQuandoUsernameDiferente() {
        UserDetails userDetails = User.builder()
                .username("um@email.com")
                .password("senha")
                .authorities(Collections.emptyList())
                .build();
        String token = jwtService.generateToken("outro@email.com");
        assertFalse(jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateTokenSemUserDetailsDeveRetornarTrueParaTokenValido() {
        String token = jwtService.generateToken("valido@email.com");
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateTokenSemUserDetailsDeveRetornarFalseParaTokenInvalido() {
        assertFalse(jwtService.validateToken("token-invalido-qualquer"));
    }

    @Test
    void extractExpirationDeveRetornarDataFutura() {
        String token = jwtService.generateToken("teste@email.com");
        assertNotNull(jwtService.extractExpiration(token));
        assertTrue(jwtService.extractExpiration(token).after(new java.util.Date()));
    }
}
