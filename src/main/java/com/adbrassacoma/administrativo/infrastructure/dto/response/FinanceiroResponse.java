package com.adbrassacoma.administrativo.infrastructure.dto.response;

import com.adbrassacoma.administrativo.domain.enums.TipoFinanceiro;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceiroResponse(
	Long id,
	BigDecimal entrada,
	BigDecimal saida,
	TipoFinanceiro tipo,
	String observacao,
	LocalDateTime dataRegistro,
	MembroFinanceiroResponse membro
) {}

