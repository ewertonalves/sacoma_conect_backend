package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.service.AuthService;
import com.adbrassacoma.administrativo.infrastructure.dto.response.AuthResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.response.UsuarioResponse;
import com.adbrassacoma.administrativo.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/cadastro retorna 201 e body com data")
    void cadastrarDeveRetornar201() throws Exception {
        String body = "{\"nome\":\"João\",\"email\":\"joao@teste.com\",\"senha\":\"senha123\"}";
        UsuarioResponse response = new UsuarioResponse(1L, "João", "joao@teste.com", Role.USER, LocalDateTime.now());
        when(authService.cadastrar(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/cadastro")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("joao@teste.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login retorna 200 e token")
    void loginDeveRetornar200() throws Exception {
        String body = "{\"email\":\"user@teste.com\",\"senha\":\"senha\"}";
        AuthResponse response = new AuthResponse("token-jwt", "Bearer", 1L, "User", "user@teste.com", Role.USER);
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("token-jwt"));
    }

    @Test
    @DisplayName("GET /api/auth/usuarios retorna 200")
    void listarTodosDeveRetornar200() throws Exception {
        when(authService.listarTodos()).thenReturn(List.of(
                new UsuarioResponse(1L, "A", "a@a.com", Role.USER, LocalDateTime.now())));

        mockMvc.perform(get("/api/auth/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("PUT /api/auth/usuarios/{id} retorna 200")
    void atualizarDeveRetornar200() throws Exception {
        String body = "{\"nome\":\"Novo\",\"email\":\"novo@teste.com\",\"senha\":null}";
        when(authService.atualizar(eq(1L), any())).thenReturn(
                new UsuarioResponse(1L, "Novo", "novo@teste.com", Role.USER, LocalDateTime.now()));

        mockMvc.perform(put("/api/auth/usuarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Novo"));
    }

    @Test
    @DisplayName("DELETE /api/auth/usuarios/{id} retorna 204")
    void deletarDeveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/auth/usuarios/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/auth/usuarios/buscar/{nome} retorna 200")
    void buscarPorNomeDeveRetornar200() throws Exception {
        when(authService.buscarPorNome("Maria")).thenReturn(List.of(
                new UsuarioResponse(1L, "Maria", "maria@teste.com", Role.USER, LocalDateTime.now())));

        mockMvc.perform(get("/api/auth/usuarios/buscar/Maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].nome").value("Maria"));
    }

    @Test
    @DisplayName("PUT /api/auth/usuarios/{id}/promover-admin retorna 200")
    void promoverParaAdminDeveRetornar200() throws Exception {
        when(authService.promoverParaAdmin(1L)).thenReturn(
                new UsuarioResponse(1L, "User", "user@teste.com", Role.ADMIN, LocalDateTime.now()));

        mockMvc.perform(put("/api/auth/usuarios/1/promover-admin").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("PUT /api/auth/usuarios/{id}/rebaixar-user retorna 200")
    void rebaixarParaUserDeveRetornar200() throws Exception {
        when(authService.rebaixarParaUser(1L)).thenReturn(
                new UsuarioResponse(1L, "User", "user@teste.com", Role.USER, LocalDateTime.now()));

        mockMvc.perform(put("/api/auth/usuarios/1/rebaixar-user").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }
}
