package com.adbrassacoma.administrativo.infrastructure.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssistenciaSocialResponse(
	Long id,
	String nomeAlimento,
	BigDecimal quantidade,
	LocalDate dataValidade,
	String familiaBeneficiada,
	BigDecimal quantidadeCestasBasicas,
	LocalDate dataEntregaCesta,
	LocalDateTime dataRegistro
) {}
