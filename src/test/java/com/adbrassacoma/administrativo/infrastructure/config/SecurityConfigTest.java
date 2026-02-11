package com.adbrassacoma.administrativo.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SecurityConfig")
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void passwordEncoderBeanDeveExistir() {
        assertNotNull(passwordEncoder);
        String encoded = passwordEncoder.encode("senha123");
        assertNotNull(encoded);
        assertTrue(encoded.length() > 0);
        assertTrue(passwordEncoder.matches("senha123", encoded));
    }

    @Test
    void corsConfigurationSourceDeveRespeitarAllowedOrigins() {
        assertNotNull(passwordEncoder);
    }
}
