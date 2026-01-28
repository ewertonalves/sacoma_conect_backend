package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.service.PermissaoService;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarPermissoesRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.PermissaoUsuarioResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.response.TelaPermissaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/permissoes")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Permissões", description = "Endpoints para gerenciamento de permissões de acesso às telas")
public class PermissaoController {

    private final PermissaoService permissaoService;

    @GetMapping("/telas")
    @Operation(summary = "Listar todas as telas disponíveis", 
               description = "Retorna uma lista com todas as telas do sistema que podem ter permissões gerenciadas")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> listarTelas() {
        List<TelaPermissaoResponse> telas = permissaoService.listarTodasTelas();
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Telas encontradas com sucesso!");
        result.put("data", telas);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/minhas")
    @Operation(summary = "Buscar minhas permissões", 
               description = "Retorna as IDs das telas que o usuário autenticado tem permissão para acessar. Qualquer usuário autenticado pode buscar suas próprias permissões.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarMinhasPermissoes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        List<String> permissoes = permissaoService.buscarMinhasPermissoes(email);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Permissões encontradas com sucesso!");
        result.put("data", permissoes);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Buscar permissões de um usuário", 
               description = "Retorna as IDs das telas que o usuário tem permissão para acessar. Apenas administradores podem usar este endpoint.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPermissoesUsuario(@PathVariable Long usuarioId) {
        List<String> permissoes = permissaoService.buscarPermissoesUsuario(usuarioId);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Permissões encontradas com sucesso!");
        result.put("data", permissoes);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usuario/{usuarioId}/completo")
    @Operation(summary = "Buscar permissões completas de um usuário", 
               description = "Retorna as permissões completas do usuário incluindo detalhes das telas")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPermissoesCompletasUsuario(@PathVariable Long usuarioId) {
        List<PermissaoUsuarioResponse> permissoes = permissaoService.buscarPermissoesCompletasUsuario(usuarioId);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Permissões encontradas com sucesso!");
        result.put("data", permissoes);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/usuario/{usuarioId}")
    @Operation(summary = "Atualizar permissões de um usuário", 
               description = "Atualiza as permissões de acesso às telas de um usuário comum. Apenas usuários com role USER podem ter permissões gerenciadas.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> atualizarPermissoes(
            @PathVariable Long usuarioId,
            @Valid @RequestBody AtualizarPermissoesRequest request) {
        permissaoService.atualizarPermissoes(usuarioId, request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Permissões atualizadas com sucesso!");
        return ResponseEntity.ok(result);
    }
}
