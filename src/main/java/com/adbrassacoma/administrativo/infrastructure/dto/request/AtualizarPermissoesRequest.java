package com.adbrassacoma.administrativo.infrastructure.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AtualizarPermissoesRequest(
    @NotNull(message = "A lista de telas permitidas não pode ser nula")
    List<String> telasPermitidas
) {}
