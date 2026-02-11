package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.enums.TipoFinanceiro;
import com.adbrassacoma.administrativo.domain.service.FinanceiroService;
import com.adbrassacoma.administrativo.infrastructure.dto.response.FinanceiroResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("FinanceiroController")
class FinanceiroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FinanceiroService financeiroService;

    @Test
    void listarTodosDeveRetornar200() throws Exception {
        FinanceiroResponse item = new FinanceiroResponse(1L, BigDecimal.ONE, BigDecimal.ZERO, TipoFinanceiro.DIZIMO, null, LocalDateTime.now(), null);
        when(financeiroService.listarTodos()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/financeiro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void buscarPorIdDeveRetornar200() throws Exception {
        FinanceiroResponse response = new FinanceiroResponse(1L, new BigDecimal("100"), BigDecimal.ZERO, TipoFinanceiro.DIZIMO, "Obs", LocalDateTime.now(), null);
        when(financeiroService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/financeiro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void buscarPorTipoDeveRetornar200() throws Exception {
        when(financeiroService.buscarPorTipo(TipoFinanceiro.DIZIMO)).thenReturn(List.of());

        mockMvc.perform(get("/api/financeiro/buscar/tipo/DIZIMO"))
                .andExpect(status().isOk());
    }

    @Test
    void cadastrarDeveRetornar201() throws Exception {
        String body = "{\"entrada\":100,\"saida\":0,\"tipo\":\"DIZIMO\",\"observacao\":\"Teste\",\"membroId\":null}";
        FinanceiroResponse saved = new FinanceiroResponse(1L, new BigDecimal("100"), BigDecimal.ZERO, TipoFinanceiro.DIZIMO, "Teste", LocalDateTime.now(), null);
        when(financeiroService.cadastrar(any())).thenReturn(saved);

        mockMvc.perform(post("/api/financeiro")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void deletarDeveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/financeiro/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
