package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.service.CepService;
import com.adbrassacoma.administrativo.infrastructure.dto.response.CepResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CepController")
class CepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CepService cepService;

    @Test
    void buscarCepDeveRetornar200EDados() throws Exception {
        CepResponse response = new CepResponse("01310100", "Av Paulista", "Bela Vista", "São Paulo", "SP", "compl");
        when(cepService.buscarCep(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/cep/01310-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.cep").value("01310100"))
                .andExpect(jsonPath("$.data.logradouro").value("Av Paulista"));
    }
}
