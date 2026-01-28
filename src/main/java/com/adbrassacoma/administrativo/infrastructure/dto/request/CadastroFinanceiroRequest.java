package com.adbrassacoma.administrativo.infrastructure.dto.request;

import com.adbrassacoma.administrativo.domain.enums.TipoFinanceiro;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CadastroFinanceiroRequest(
	@DecimalMin(value = "0.0", inclusive = true, message = "Entrada deve ser maior ou igual a zero")
	BigDecimal entrada,
	
	@DecimalMin(value = "0.0", inclusive = true, message = "Saída deve ser maior ou igual a zero")
	BigDecimal saida,
	
	@NotNull(message = "Tipo é obrigatório")
	TipoFinanceiro tipo,
	
	@Size(max = 255, message = "Observação deve ter no máximo 255 caracteres")
	String observacao,
	
	Long membroId
) {}

