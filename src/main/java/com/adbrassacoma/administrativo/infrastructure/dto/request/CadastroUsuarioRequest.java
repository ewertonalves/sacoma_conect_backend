package com.adbrassacoma.administrativo.infrastructure.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequest(
	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
	String nome,
	
	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email deve ser válido")
	@Size(max = 120, message = "Email deve ter no máximo 120 caracteres")
	String email,
	
	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
	String senha
) {}

