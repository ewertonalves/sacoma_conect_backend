package com.adbrassacoma.administrativo.infrastructure.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record CadastroMembroRequest(
	@Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
	String nome,

	@Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
	String rg,

	@Size(max = 14, message = "CPF deve ter no máximo 14 caracteres")
	String cpf,

	@Size(max = 20, message = "RI deve ter no máximo 20 caracteres")
	String ri,

	@Size(max = 60, message = "Cargo deve ter no máximo 60 caracteres")
	String cargo,

	@Valid
	EnderecoRequest endereco
) {}

