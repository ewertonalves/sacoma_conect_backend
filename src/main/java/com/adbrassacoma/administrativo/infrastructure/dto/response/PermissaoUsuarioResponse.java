package com.adbrassacoma.administrativo.infrastructure.dto.response;

public record PermissaoUsuarioResponse(
    Long id,
    Long usuarioId,
    String telaId,
    TelaPermissaoResponse tela
) {}
