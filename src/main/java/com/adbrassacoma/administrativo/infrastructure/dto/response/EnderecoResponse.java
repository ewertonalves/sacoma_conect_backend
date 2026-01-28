package com.adbrassacoma.administrativo.infrastructure.dto.response;

public record EnderecoResponse(
	Long id,
	String rua,
	String numero,
	String cep,
	String bairro,
	String cidade,
	String estado,
	String complemento
) {}

