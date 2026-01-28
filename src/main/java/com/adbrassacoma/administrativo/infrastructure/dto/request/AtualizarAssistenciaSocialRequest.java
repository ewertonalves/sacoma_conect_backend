package com.adbrassacoma.administrativo.infrastructure.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AtualizarAssistenciaSocialRequest(
	String nomeAlimento,
	
	@DecimalMin(value = "0.01", message = "Quantidade deve ser maior que zero")
	BigDecimal quantidade,
	
	LocalDate dataValidade,
	
	@Size(max = 255, message = "Família beneficiada deve ter no máximo 255 caracteres")
	String familiaBeneficiada,
	
	@DecimalMin(value = "0.0", inclusive = true, message = "Quantidade de cestas básicas deve ser maior ou igual a zero")
	BigDecimal quantidadeCestasBasicas,
	
	LocalDate dataEntregaCesta
) {}
