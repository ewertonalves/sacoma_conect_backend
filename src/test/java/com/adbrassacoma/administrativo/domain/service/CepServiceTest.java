package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.infrastructure.client.CorreiosClient;
import com.adbrassacoma.administrativo.infrastructure.client.dto.CorreiosCepResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.response.CepResponse;
import com.adbrassacoma.administrativo.infrastructure.exception.CepNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CepService")
class CepServiceTest {

    @Mock
    private CorreiosClient correiosClient;

    @InjectMocks
    private CepService cepService;

    @Test
    void buscarCepDeveLancarQuandoCepComMenosDe8Digitos() {
        assertThrows(IllegalArgumentException.class, () -> cepService.buscarCep("1234567"));
        assertThrows(IllegalArgumentException.class, () -> cepService.buscarCep("12345"));
        verify(correiosClient, never()).buscarCep(anyString());
    }

    @Test
    void buscarCepDeveRetornarResponseQuandoEncontrado() {
        CorreiosCepResponse apiResponse = new CorreiosCepResponse(
                "01310100",
                "Avenida Paulista",
                "lado ímpar",
                "Bela Vista",
                "São Paulo",
                "SP",
                false
        );
        when(correiosClient.buscarCep("01310100")).thenReturn(apiResponse);

        CepResponse response = cepService.buscarCep("01310-100");

        assertNotNull(response);
        assertEquals("01310100", response.cep());
        assertEquals("Avenida Paulista", response.logradouro());
        assertEquals("Bela Vista", response.bairro());
        assertEquals("São Paulo", response.localidade());
        assertEquals("SP", response.uf());
    }

    @Test
    void buscarCepDeveLancarCepNaoEncontradoQuandoApiRetornaErro() {
        when(correiosClient.buscarCep("00000000")).thenReturn(
                new CorreiosCepResponse(null, null, null, null, null, null, true));

        assertThrows(CepNaoEncontradoException.class, () -> cepService.buscarCep("00000000"));
    }

    @Test
    void buscarCepDeveTratarCamposNulosNaResposta() {
        when(correiosClient.buscarCep("01310100")).thenReturn(
                new CorreiosCepResponse("01310100", null, null, null, null, null, false));

        CepResponse response = cepService.buscarCep("01310100");

        assertEquals("", response.logradouro());
        assertEquals("", response.bairro());
        assertEquals("", response.localidade());
        assertEquals("", response.uf());
    }

    @Test
    void buscarCepDeveLancarCepNaoEncontradoQuandoClientLancaExcecao() {
        when(correiosClient.buscarCep(anyString())).thenThrow(new RuntimeException("Timeout"));

        assertThrows(CepNaoEncontradoException.class, () -> cepService.buscarCep("01310100"));
    }
}
