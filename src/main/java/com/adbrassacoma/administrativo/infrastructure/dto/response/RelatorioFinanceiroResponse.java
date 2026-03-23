package com.adbrassacoma.administrativo.infrastructure.dto.response;

import com.adbrassacoma.administrativo.domain.enums.TipoPeriodoRelatorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RelatorioFinanceiroResponse(
        LocalDate dataInicial,
        LocalDate dataFinal,
        TipoPeriodoRelatorio tipoPeriodo,
        List<FinanceiroResponse> itens,
        BigDecimal totalEntrada,
        BigDecimal totalSaida,
        BigDecimal saldo
) {}
