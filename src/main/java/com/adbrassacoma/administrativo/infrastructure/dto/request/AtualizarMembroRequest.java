package com.adbrassacoma.administrativo.infrastructure.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarMembroRequest(
	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
	String nome,
	
	@NotBlank(message = "RG é obrigatório")
	@Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
	String rg,
	
	@Size(max = 20, message = "RI deve ter no máximo 20 caracteres")
	String ri,
	
	@Size(max = 60, message = "Cargo deve ter no máximo 60 caracteres")
	String cargo,
	
	@Valid
	EnderecoRequest endereco
) {}

