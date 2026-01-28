package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.service.CepService;
import com.adbrassacoma.administrativo.infrastructure.dto.response.CepResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cep")
@Tag(name = "CEP", description = "Endpoints para busca de CEP")
public class CepController {
    
    private final CepService cepService;

    @GetMapping("/{cep}")
    @Operation(summary = "Buscar endereço por CEP", description = "Retorna os dados do endereço baseado no CEP informado")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarCep(@PathVariable String cep) {
        CepResponse response = cepService.buscarCep(cep);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "CEP encontrado com sucesso!");
        result.put("data", response);
        return ResponseEntity.ok(result);
    }
}

