package com.adbrassacoma.administrativo.infrastructure.dto.response;

import com.adbrassacoma.administrativo.domain.enums.CategoriaFinanceira;
import com.adbrassacoma.administrativo.domain.enums.TipoMovimentacaoFinanceira;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceiroResponse(
        Long id,
        Integer codigoFinanceiro,
        String descricaoCodigoFinanceiro,
        TipoMovimentacaoFinanceira tipo,
        CategoriaFinanceira categoria,
        BigDecimal entrada,
        BigDecimal saida,
        String observacao,
        LocalDateTime dataRegistro,
        MembroFinanceiroResponse membro) {
}
