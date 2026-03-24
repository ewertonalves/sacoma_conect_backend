package com.adbrassacoma.administrativo.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TipoMovimentacaoFinanceira")
class TipoMovimentacaoFinanceiraTest {

    @Test
    void deveConterEntradaESaida() {
        TipoMovimentacaoFinanceira[] values = TipoMovimentacaoFinanceira.values();
        assertEquals(2, values.length);
        assertEquals(TipoMovimentacaoFinanceira.ENTRADA, values[0]);
        assertEquals(TipoMovimentacaoFinanceira.SAIDA, values[1]);
    }

    @Test
    void valueOfDeveResolverPeloNome() {
        assertEquals(TipoMovimentacaoFinanceira.ENTRADA, TipoMovimentacaoFinanceira.valueOf("ENTRADA"));
        assertEquals(TipoMovimentacaoFinanceira.SAIDA, TipoMovimentacaoFinanceira.valueOf("SAIDA"));
    }
}
