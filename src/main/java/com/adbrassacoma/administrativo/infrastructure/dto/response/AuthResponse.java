package com.adbrassacoma.administrativo.infrastructure.dto.response;

import com.adbrassacoma.administrativo.domain.enums.Role;

public record AuthResponse(
	String token,
	String tipo,
	Long usuarioId,
	String nome,
	String email,
	Role role
) {}

