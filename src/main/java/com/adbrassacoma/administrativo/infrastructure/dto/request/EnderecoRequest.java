package com.adbrassacoma.administrativo.infrastructure.dto.request;

import jakarta.validation.constraints.Size;

public record EnderecoRequest(
	@Size(max = 120, message = "Rua deve ter no máximo 120 caracteres")
	String rua,

	@Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
	String numero,

	@Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
	String cep,

	@Size(max = 80, message = "Bairro deve ter no máximo 80 caracteres")
	String bairro,

	@Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
	String cidade,

	@Size(max = 2, message = "Estado deve ter no máximo 2 caracteres")
	String estado,

	@Size(max = 120, message = "Complemento deve ter no máximo 120 caracteres")
	String complemento
) {}

