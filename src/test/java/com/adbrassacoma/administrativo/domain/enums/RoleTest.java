package com.adbrassacoma.administrativo.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role")
class RoleTest {

    @Test
    void deveTerValoresAdminEUser() {
        Role[] values = Role.values();
        assertEquals(2, values.length);
        assertEquals(Role.ADMIN, values[0]);
        assertEquals(Role.USER, values[1]);
    }

    @Test
    void valueOfDeveRetornarEnumCorreto() {
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.USER, Role.valueOf("USER"));
    }

    @Test
    void valueOfDeveLancarQuandoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Role.valueOf("INVALIDO"));
    }
}
