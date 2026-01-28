package com.adbrassacoma.administrativo.infrastructure.dto.response;

public record CepResponse(
    String cep,
    String logradouro,
    String bairro,
    String localidade,
    String uf,
    String complemento
) {}

