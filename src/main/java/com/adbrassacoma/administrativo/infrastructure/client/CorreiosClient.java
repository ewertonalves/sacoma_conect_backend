package com.adbrassacoma.administrativo.infrastructure.client;

import com.adbrassacoma.administrativo.infrastructure.client.dto.CorreiosCepResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "correios", url = "${correios.api.url:https://viacep.com.br}")
public interface CorreiosClient {

    @GetMapping("/ws/{cep}/json")
    CorreiosCepResponse buscarCep(@PathVariable("cep") String cep);
}

