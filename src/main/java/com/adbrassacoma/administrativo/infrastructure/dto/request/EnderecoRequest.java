package com.adbrassacoma.administrativo.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(
	@NotBlank(message = "Rua é obrigatória")
	@Size(max = 120, message = "Rua deve ter no máximo 120 caracteres")
	String rua,
	
	@NotBlank(message = "Número é obrigatório")
	@Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
	String numero,
	
	@NotBlank(message = "CEP é obrigatório")
	@Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
	String cep,
	
	@NotBlank(message = "Bairro é obrigatório")
	@Size(max = 80, message = "Bairro deve ter no máximo 80 caracteres")
	String bairro,
	
	@NotBlank(message = "Cidade é obrigatória")
	@Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
	String cidade,
	
	@NotBlank(message = "Estado é obrigatório")
	@Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
	String estado,
	
	@Size(max = 120, message = "Complemento deve ter no máximo 120 caracteres")
	String complemento
) {}

