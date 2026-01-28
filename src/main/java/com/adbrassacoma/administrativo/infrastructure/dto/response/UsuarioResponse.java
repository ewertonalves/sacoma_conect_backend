package com.adbrassacoma.administrativo.infrastructure.dto.response;

import com.adbrassacoma.administrativo.domain.enums.Role;
import java.time.LocalDateTime;

public record UsuarioResponse(
	Long id,
	String nome,
	String email,
	Role role,
	LocalDateTime dataCriacao
) {}

