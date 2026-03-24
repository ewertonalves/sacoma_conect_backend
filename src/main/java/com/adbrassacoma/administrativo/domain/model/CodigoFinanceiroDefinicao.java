package com.adbrassacoma.administrativo.domain.model;

import com.adbrassacoma.administrativo.domain.enums.CategoriaFinanceira;
import com.adbrassacoma.administrativo.domain.enums.TipoMovimentacaoFinanceira;

public record CodigoFinanceiroDefinicao(
        int codigo,
        String descricao,
        TipoMovimentacaoFinanceira tipo,
        CategoriaFinanceira categoria) {
}
