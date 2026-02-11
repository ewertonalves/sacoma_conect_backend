package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.service.AssistenciaSocialService;
import com.adbrassacoma.administrativo.infrastructure.dto.response.AssistenciaSocialResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AssistenciaSocialController")
class AssistenciaSocialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssistenciaSocialService assistenciaSocialService;

    @Test
    void listarTodosDeveRetornar200ComPaginacao() throws Exception {
        AssistenciaSocialResponse item = new AssistenciaSocialResponse(1L, "Arroz", new BigDecimal("50"),
                LocalDate.now(), "Família", new BigDecimal("2"), LocalDate.now(), LocalDateTime.now());
        when(assistenciaSocialService.listarTodos(any(), eq(null))).thenReturn(new PageImpl<>(List.of(item)));

        mockMvc.perform(get("/api/assistencia-social"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalItems").value(1));
    }

    @Test
    void buscarPorIdDeveRetornar200() throws Exception {
        AssistenciaSocialResponse response = new AssistenciaSocialResponse(1L, "Arroz", new BigDecimal("50"),
                LocalDate.now(), null, null, null, LocalDateTime.now());
        when(assistenciaSocialService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/assistencia-social/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nomeAlimento").value("Arroz"));
    }

    @Test
    void cadastrarDeveRetornar201() throws Exception {
        String body = "{\"nomeAlimento\":\"Feijão\",\"quantidade\":10,\"dataValidade\":\"2025-12-31\",\"familiaBeneficiada\":\"F\",\"quantidadeCestasBasicas\":1}";
        AssistenciaSocialResponse saved = new AssistenciaSocialResponse(1L, "Feijão", new BigDecimal("10"),
                LocalDate.parse("2025-12-31"), "F", new BigDecimal("1"), null, LocalDateTime.now());
        when(assistenciaSocialService.cadastrar(any())).thenReturn(saved);

        mockMvc.perform(post("/api/assistencia-social")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void deletarDeveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/assistencia-social/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
