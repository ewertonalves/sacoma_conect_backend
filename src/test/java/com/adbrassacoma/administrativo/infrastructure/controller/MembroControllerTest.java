package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.service.MembroService;
import com.adbrassacoma.administrativo.infrastructure.dto.response.EnderecoResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.response.MembroResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MembroController")
class MembroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MembroService membroService;

    private static EnderecoResponse enderecoResponse() {
        return new EnderecoResponse(1L, "Rua A", "100", "01310100", "Centro", "São Paulo", "SP", null);
    }

    @Test
    void listarTodosDeveRetornar200() throws Exception {
        MembroResponse item = new MembroResponse(1L, "João", "RG", "111.444.777-35", "RI", "Pastor", enderecoResponse());
        when(membroService.listarTodos()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/membros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].nome").value("João"));
    }

    @Test
    void buscarPorIdDeveRetornar200() throws Exception {
        MembroResponse response = new MembroResponse(1L, "João", "RG", "111.444.777-35", "RI", "Pastor", enderecoResponse());
        when(membroService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/membros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void cadastrarDeveRetornar201() throws Exception {
        String body = "{\"nome\":\"João\",\"rg\":\"RG1\",\"cpf\":\"111.444.777-35\",\"ri\":\"RI1\",\"cargo\":\"Pastor\",\"endereco\":{\"rua\":\"Rua A\",\"numero\":\"100\",\"cep\":\"01310100\",\"bairro\":\"Centro\",\"cidade\":\"São Paulo\",\"estado\":\"SP\"}}";
        MembroResponse saved = new MembroResponse(1L, "João", "RG1", "111.444.777-35", "RI1", "Pastor", enderecoResponse());
        when(membroService.cadastrar(any())).thenReturn(saved);

        mockMvc.perform(post("/api/membros")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void deletarDeveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/membros/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarPorNomeDeveRetornar200() throws Exception {
        MembroResponse item = new MembroResponse(1L, "João", "RG", "111.444.777-35", "RI", "Pastor", enderecoResponse());
        when(membroService.buscarPorNome("João")).thenReturn(List.of(item));

        mockMvc.perform(get("/api/membros/buscar/nome/João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void buscarPorCpfDeveRetornar200() throws Exception {
        MembroResponse response = new MembroResponse(1L, "João", "RG", "111.444.777-35", "RI", "Pastor", enderecoResponse());
        when(membroService.buscarPorCpf("111.444.777-35")).thenReturn(response);

        mockMvc.perform(get("/api/membros/buscar/cpf/111.444.777-35"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cpf").value("111.444.777-35"));
    }

    @Test
    void atualizarDeveRetornar200() throws Exception {
        String body = "{\"nome\":\"João Atualizado\",\"rg\":\"RG2\",\"ri\":\"RI2\",\"cargo\":\"Pastor\",\"endereco\":{\"rua\":\"Rua B\",\"numero\":\"200\",\"cep\":\"01310100\",\"bairro\":\"Centro\",\"cidade\":\"São Paulo\",\"estado\":\"SP\"}}";
        MembroResponse updated = new MembroResponse(1L, "João Atualizado", "RG2", "111.444.777-35", "RI2", "Pastor", enderecoResponse());
        when(membroService.atualizar(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/membros/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("João Atualizado"));
    }
}
