package com.adbrassacoma.administrativo.infrastructure.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroMembroRequest(
	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
	String nome,
	
	@Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
	String rg,
	
	@NotBlank(message = "CPF é obrigatório")
	@Size(max = 14, message = "CPF deve ter no máximo 14 caracteres")
	String cpf,
	
    @NotBlank(message = "RI é obrigatório")
	@Size(max = 20, message = "RI deve ter no máximo 20 caracteres")
	String ri,
	
	@Size(max = 60, message = "Cargo deve ter no máximo 60 caracteres")
	String cargo,
	
	@NotNull(message = "Endereço é obrigatório")
	@Valid
	EnderecoRequest endereco
) {}

