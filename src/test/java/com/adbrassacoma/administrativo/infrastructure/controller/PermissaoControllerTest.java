package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.service.PermissaoService;
import com.adbrassacoma.administrativo.infrastructure.dto.response.TelaPermissaoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PermissaoController")
class PermissaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissaoService permissaoService;

    @Test
    void listarTelasDeveRetornar200() throws Exception {
        TelaPermissaoResponse tela = new TelaPermissaoResponse("membros", "Membros", "/membros", "Desc");
        when(permissaoService.listarTodasTelas()).thenReturn(List.of(tela));

        mockMvc.perform(get("/api/permissoes/telas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("membros"));
    }

    @Test
    void buscarPermissoesUsuarioDeveRetornar200() throws Exception {
        when(permissaoService.buscarPermissoesUsuario(1L)).thenReturn(List.of("membros", "financeiro"));

        mockMvc.perform(get("/api/permissoes/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void atualizarPermissoesDeveRetornar200() throws Exception {
        String body = "{\"telasPermitidas\":[\"membros\"]}";

        mockMvc.perform(put("/api/permissoes/usuario/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}
