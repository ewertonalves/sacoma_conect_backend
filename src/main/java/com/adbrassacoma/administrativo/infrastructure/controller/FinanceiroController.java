package com.adbrassacoma.administrativo.infrastructure.controller;

import com.adbrassacoma.administrativo.domain.enums.TipoFinanceiro;
import com.adbrassacoma.administrativo.domain.service.FinanceiroService;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarFinanceiroRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroFinanceiroRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.FinanceiroResponse;
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
@RequestMapping("/api/financeiro")
@Tag(name = "Financeiro", description = "Endpoints para gerenciamento financeiro")
public class FinanceiroController {
    
    private final FinanceiroService financeiroService;

    @PostMapping
    @Operation(summary = "Cadastrar novo registro financeiro", description = "Cria um novo registro financeiro no sistema")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> cadastrar(@Valid @RequestBody CadastroFinanceiroRequest request) {
        FinanceiroResponse response = financeiroService.cadastrar(request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registro financeiro cadastrado com sucesso!");
        result.put("data", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    @Operation(summary = "Listar todos os registros financeiros", description = "Retorna uma lista com todos os registros financeiros cadastrados")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> listarTodos() {
        List<FinanceiroResponse> financeiros = financeiroService.listarTodos();
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registros financeiros encontrados com sucesso!");
        result.put("data", financeiros);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar registro financeiro por ID", description = "Busca um registro financeiro pelo seu ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        FinanceiroResponse financeiro = financeiroService.buscarPorId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registro financeiro encontrado com sucesso!");
        result.put("data", financeiro);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buscar/tipo/{tipo}")
    @Operation(summary = "Buscar registros financeiros por tipo", description = "Busca registros financeiros pelo tipo informado")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPorTipo(@PathVariable TipoFinanceiro tipo) {
        List<FinanceiroResponse> financeiros = financeiroService.buscarPorTipo(tipo);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registros financeiros encontrados com sucesso!");
        result.put("data", financeiros);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buscar/membro/{membroId}")
    @Operation(summary = "Buscar registros financeiros por membro", description = "Busca todos os registros financeiros de um membro específico")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> buscarPorMembro(@PathVariable Long membroId) {
        List<FinanceiroResponse> financeiros = financeiroService.buscarPorMembro(membroId);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registros financeiros encontrados com sucesso!");
        result.put("data", financeiros);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar registro financeiro", description = "Atualiza os dados de um registro financeiro existente")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarFinanceiroRequest request) {
        FinanceiroResponse financeiro = financeiroService.atualizar(id, request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registro financeiro atualizado com sucesso!");
        result.put("data", financeiro);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar registro financeiro", description = "Remove um registro financeiro do sistema")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable Long id) {
        financeiroService.deletar(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Registro financeiro deletado com sucesso!");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }
}

