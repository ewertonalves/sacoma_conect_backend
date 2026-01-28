package com.adbrassacoma.administrativo.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CorreiosCepResponse(
    @JsonProperty("cep")
    String cep,
    
    @JsonProperty("logradouro")
    String logradouro,
    
    @JsonProperty("complemento")
    String complemento,
    
    @JsonProperty("bairro")
    String bairro,
    
    @JsonProperty("localidade")
    String localidade,
    
    @JsonProperty("uf")
    String uf,
    
    @JsonProperty("erro")
    Boolean erro
) {}

