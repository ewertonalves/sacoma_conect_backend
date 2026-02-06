package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.service.AuthService;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarUsuarioRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.AuthResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroUsuarioRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.LoginRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Autenticação", description = "Endpoints para autenticação e gerenciamento de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/cadastro")
    @Operation(summary = "Cadastrar novo usuário", description = "Cria um novo usuário no sistema")
    @SecurityRequirement(name = "")
    public ResponseEntity<Map<String, Object>> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
        UsuarioResponse response = authService.cadastrar(request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Usuário cadastrado com sucesso!");
        result.put("data", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/login")
    @Operation(summary = "Fazer login", description = "Autentica um usuário e retorna um token JWT")
    @SecurityRequirement(name = "")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Login realizado com sucesso!");
        result.put("data", response);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista com todos os usuários cadastrados")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> listarTodos() {
        List<UsuarioResponse> usuarios = authService.listarTodos();
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Usuários encontrados com sucesso!");
        result.put("data", usuarios);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usuarios/buscar/{nome}")
    @Operation(summary = "Buscar usuários por nome", description = "Busca usuário por nome")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPorNome(@PathVariable String nome) {
        List<UsuarioResponse> usuarios = authService.buscarPorNome(nome);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Usuários encontrados com sucesso!");
        result.put("data", usuarios);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable Long id,
            @Valid @RequestBody AtualizarUsuarioRequest request) {
        UsuarioResponse usuario = authService.atualizar(id, request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Usuário atualizado com sucesso!");
        result.put("data", usuario);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable Long id) {
        authService.deletar(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Usuário deletado com sucesso!");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }

    @PutMapping("/usuarios/{id}/promover-admin")
    @Operation(summary = "Promover usuário a admin", description = "Promove um usuário comum para administrador. Apenas admins podem executar esta ação.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> promoverParaAdmin(@PathVariable Long id) {
        UsuarioResponse usuario = authService.promoverParaAdmin(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Usuário promovido a administrador com sucesso!");
        result.put("data", usuario);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/usuarios/{id}/rebaixar-user")
    @Operation(summary = "Rebaixar admin para usuário comum", description = "Rebaixa um administrador para usuário comum. Apenas o administrador master pode executar esta ação.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> rebaixarParaUser(@PathVariable Long id) {
        UsuarioResponse usuario = authService.rebaixarParaUser(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Administrador rebaixado a usuário comum com sucesso!");
        result.put("data", usuario);
        return ResponseEntity.ok(result);
    }
}
