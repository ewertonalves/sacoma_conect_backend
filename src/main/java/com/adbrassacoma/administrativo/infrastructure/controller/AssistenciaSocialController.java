package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.service.AssistenciaSocialService;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarAssistenciaSocialRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroAssistenciaSocialRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.AssistenciaSocialResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/assistencia-social")
@Tag(name = "Assistência Social", description = "Endpoints para gerenciamento de assistência social")
public class AssistenciaSocialController {
    
    private final AssistenciaSocialService assistenciaSocialService;

    @PostMapping
    @Operation(summary = "Cadastrar novo registro de assistência social", description = "Cria um novo registro de assistência social no sistema")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> cadastrar(@Valid @RequestBody CadastroAssistenciaSocialRequest request) {
        AssistenciaSocialResponse response = assistenciaSocialService.cadastrar(request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registro de assistência social cadastrado com sucesso!");
        result.put("data", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    @Operation(summary = "Listar registros de assistência social", description = "Retorna uma lista paginada de registros de assistência social com busca dinâmica")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) String search) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AssistenciaSocialResponse> assistenciaSocialPage = assistenciaSocialService.listarTodos(pageable, search);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registros de assistência social encontrados com sucesso!");
        result.put("data", assistenciaSocialPage.getContent());
        result.put("currentPage", assistenciaSocialPage.getNumber());
        result.put("totalItems", assistenciaSocialPage.getTotalElements());
        result.put("totalPages", assistenciaSocialPage.getTotalPages());
        result.put("pageSize", assistenciaSocialPage.getSize());
        result.put("hasNext", assistenciaSocialPage.hasNext());
        result.put("hasPrevious", assistenciaSocialPage.hasPrevious());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar registro de assistência social por ID", description = "Busca um registro de assistência social pelo seu ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        AssistenciaSocialResponse assistenciaSocial = assistenciaSocialService.buscarPorId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registro de assistência social encontrado com sucesso!");
        result.put("data", assistenciaSocial);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar registro de assistência social", description = "Atualiza os dados de um registro de assistência social existente")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarAssistenciaSocialRequest request) {
        AssistenciaSocialResponse assistenciaSocial = assistenciaSocialService.atualizar(id, request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registro de assistência social atualizado com sucesso!");
        result.put("data", assistenciaSocial);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar registro de assistência social", description = "Remove um registro de assistência social do sistema")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable Long id) {
        assistenciaSocialService.deletar(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registro de assistência social deletado com sucesso!");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }
}
