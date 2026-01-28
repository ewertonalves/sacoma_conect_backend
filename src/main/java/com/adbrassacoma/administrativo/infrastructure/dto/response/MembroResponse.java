package com.adbrassacoma.administrativo.infrastructure.dto.response;

public record MembroResponse(
	Long id,
	String nome,
	String rg,
	String cpf,
	String ri,
	String cargo,
	EnderecoResponse endereco
) {}

