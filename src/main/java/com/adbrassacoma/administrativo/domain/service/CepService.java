package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.infrastructure.client.CorreiosClient;
import com.adbrassacoma.administrativo.infrastructure.client.dto.CorreiosCepResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.response.CepResponse;
import com.adbrassacoma.administrativo.infrastructure.exception.CepNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CepService {

    private final CorreiosClient correiosClient;

    public CepResponse buscarCep(String cep) {
        log.info("Buscando CEP: {}", cep);
        
        // Remove formatação do CEP
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        
        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP deve conter 8 dígitos");
        }

        try {
            CorreiosCepResponse response = correiosClient.buscarCep(cepLimpo);
            
            if (response.erro() != null && response.erro()) {
                log.warn("CEP não encontrado: {}", cep);
                throw new CepNaoEncontradoException("CEP não encontrado: " + cep);
            }

            log.info("CEP encontrado: {} - {}", cep, response.logradouro());
            
            return new CepResponse(
                response.cep(),
                response.logradouro() != null ? response.logradouro() : "",
                response.bairro() != null ? response.bairro() : "",
                response.localidade() != null ? response.localidade() : "",
                response.uf() != null ? response.uf() : "",
                response.complemento() != null ? response.complemento() : ""
            );
        } catch (Exception e) {
            log.error("Erro ao buscar CEP: {}", cep, e);
            if (e instanceof CepNaoEncontradoException) {
                throw e;
            }
            throw new CepNaoEncontradoException("Erro ao buscar CEP: " + e.getMessage());
        }
    }
}

