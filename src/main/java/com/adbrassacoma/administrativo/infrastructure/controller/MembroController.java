package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.service.MembroService;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarMembroRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroMembroRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.MembroResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/membros")
@Tag(name = "Membros", description = "Endpoints para gerenciamento de membros")
public class MembroController {
    
    private final MembroService membroService;

    @PostMapping
    @Operation(summary = "Cadastrar novo membro", description = "Cria um novo membro no sistema")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> cadastrar(@Valid @RequestBody CadastroMembroRequest request) {
        MembroResponse response = membroService.cadastrar(request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Membro cadastrado com sucesso!");
        result.put("data", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    @Operation(summary = "Listar todos os membros", description = "Retorna uma lista com todos os membros cadastrados")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> listarTodos() {
        List<MembroResponse> membros = membroService.listarTodos();
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Membros encontrados com sucesso!");
        result.put("data", membros);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar membro por ID", description = "Busca um membro pelo seu ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        MembroResponse membro = membroService.buscarPorId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Membro encontrado com sucesso!");
        result.put("data", membro);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buscar/nome/{nome}")
    @Operation(summary = "Buscar membros por nome", description = "Busca membros cujo nome contém o termo informado")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPorNome(@PathVariable String nome) {
        List<MembroResponse> membros = membroService.buscarPorNome(nome);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Membros encontrados com sucesso!");
        result.put("data", membros);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buscar/cpf/{cpf}")
    @Operation(summary = "Buscar membro por CPF", description = "Busca um membro pelo seu CPF")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPorCpf(@PathVariable String cpf) {
        MembroResponse membro = membroService.buscarPorCpf(cpf);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Membro encontrado com sucesso!");
        result.put("data", membro);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buscar/ri/{ri}")
    @Operation(summary = "Buscar membro por RI", description = "Busca um membro pelo seu RI")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPorRi(@PathVariable String ri) {
        MembroResponse membro = membroService.buscarPorRi(ri);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Membro encontrado com sucesso!");
        result.put("data", membro);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar membro", description = "Atualiza os dados de um membro existente")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarMembroRequest request) {
        MembroResponse membro = membroService.atualizar(id, request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Membro atualizado com sucesso!");
        result.put("data", membro);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar membro", description = "Remove um membro do sistema")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable Long id) {
        membroService.deletar(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Membro deletado com sucesso!");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }
}
