package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.model.TelaPermissao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("TelaPermissaoDiscoveryService")
class TelaPermissaoDiscoveryServiceTest {

    @Autowired
    private TelaPermissaoDiscoveryService discoveryService;

    @Test
    void descobrirTelasDeveRetornarListaNaoVazia() {
        List<TelaPermissao> telas = discoveryService.descobrirTelas();

        assertNotNull(telas);
        assertFalse(telas.isEmpty());
    }

    @Test
    void descobrirTelasDeveIncluirDashboard() {
        List<TelaPermissao> telas = discoveryService.descobrirTelas();

        boolean hasDashboard = telas.stream()
                .anyMatch(t -> "dashboard".equals(t.getId()));
        assertTrue(hasDashboard, "Deveria conter tela dashboard");
    }

    @Test
    void descobrirTelasDeveRetornarTelasComIdNomeERota() {
        List<TelaPermissao> telas = discoveryService.descobrirTelas();

        for (TelaPermissao tela : telas) {
            assertNotNull(tela.getId());
            assertNotNull(tela.getNome());
            assertNotNull(tela.getRota());
        }
    }
}
