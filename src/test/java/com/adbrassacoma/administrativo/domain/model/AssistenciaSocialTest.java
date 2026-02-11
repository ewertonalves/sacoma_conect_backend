package com.adbrassacoma.administrativo.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AssistenciaSocial")
class AssistenciaSocialTest {

    @Test
    void builderDeveCriarEntidadeComTodosCampos() {
        LocalDate validade = LocalDate.now().plusMonths(1);
        LocalDateTime registro = LocalDateTime.now();
        AssistenciaSocial a = AssistenciaSocial.builder()
                .id(1L)
                .nomeAlimento("Arroz")
                .quantidade(new BigDecimal("50.00"))
                .dataValidade(validade)
                .familiaBeneficiada("Família X")
                .quantidadeCestasBasicas(new BigDecimal("2"))
                .dataEntregaCesta(LocalDate.now())
                .dataRegistro(registro)
                .build();

        assertEquals(1L, a.getId());
        assertEquals("Arroz", a.getNomeAlimento());
        assertEquals(0, new BigDecimal("50.00").compareTo(a.getQuantidade()));
        assertEquals(validade, a.getDataValidade());
        assertEquals("Família X", a.getFamiliaBeneficiada());
        assertEquals(registro, a.getDataRegistro());
    }

    @Test
    void settersDevemFuncionar() {
        AssistenciaSocial a = new AssistenciaSocial();
        a.setId(2L);
        a.setNomeAlimento("Feijão");
        a.setQuantidade(BigDecimal.ONE);
        a.setDataValidade(LocalDate.now());
        a.setFamiliaBeneficiada("Y");
        a.setDataRegistro(LocalDateTime.now());

        assertEquals(2L, a.getId());
        assertEquals("Feijão", a.getNomeAlimento());
        assertEquals(BigDecimal.ONE, a.getQuantidade());
    }
}
