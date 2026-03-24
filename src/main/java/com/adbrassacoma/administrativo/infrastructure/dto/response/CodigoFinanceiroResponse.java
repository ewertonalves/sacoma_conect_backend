package com.adbrassacoma.administrativo.infrastructure.dto.response;

import com.adbrassacoma.administrativo.domain.enums.CategoriaFinanceira;
import com.adbrassacoma.administrativo.domain.enums.TipoMovimentacaoFinanceira;

public record CodigoFinanceiroResponse(
        int codigo,
        String descricao,
        TipoMovimentacaoFinanceira tipo,
        CategoriaFinanceira categoria) {
}
