package com.adbrassacoma.administrativo.infrastructure.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AtualizarFinanceiroRequest(
        @NotNull(message = "Código financeiro é obrigatório")
        Integer codigoFinanceiro,

        @DecimalMin(value = "0.0", inclusive = true, message = "Entrada deve ser maior ou igual a zero")
        BigDecimal entrada,

        @DecimalMin(value = "0.0", inclusive = true, message = "Saída deve ser maior ou igual a zero")
        BigDecimal saida,

        @Size(max = 255, message = "Observação deve ter no máximo 255 caracteres")
        String observacao,

        Long membroId) {
}
