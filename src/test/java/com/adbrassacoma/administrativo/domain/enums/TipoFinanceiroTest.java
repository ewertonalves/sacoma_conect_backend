package com.adbrassacoma.administrativo.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TipoFinanceiro")
class TipoFinanceiroTest {

    @Test
    void deveTerValoresEsperados() {
        TipoFinanceiro[] values = TipoFinanceiro.values();
        assertEquals(4, values.length);
        assertEquals(TipoFinanceiro.DIZIMO, values[0]);
        assertEquals(TipoFinanceiro.DESPESAS, values[1]);
        assertEquals(TipoFinanceiro.REFORMAS, values[2]);
        assertEquals(TipoFinanceiro.OFERTAS, values[3]);
    }

    @Test
    void valueOfDeveRetornarEnumCorreto() {
        assertEquals(TipoFinanceiro.DIZIMO, TipoFinanceiro.valueOf("DIZIMO"));
        assertEquals(TipoFinanceiro.OFERTAS, TipoFinanceiro.valueOf("OFERTAS"));
    }
}
