package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.enums.Role;
import com.adbrassacoma.administrativo.domain.model.PermissaoUsuario;
import com.adbrassacoma.administrativo.domain.model.TelaPermissao;
import com.adbrassacoma.administrativo.domain.model.Usuario;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarPermissoesRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.PermissaoUsuarioResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.response.TelaPermissaoResponse;
import com.adbrassacoma.administrativo.infrastructure.exception.UsuarioNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.repository.PermissaoUsuarioRepository;
import com.adbrassacoma.administrativo.infrastructure.repository.TelaPermissaoRepository;
import com.adbrassacoma.administrativo.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissaoService {

    private final TelaPermissaoRepository telaPermissaoRepository;
    private final PermissaoUsuarioRepository permissaoUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<TelaPermissaoResponse> listarTodasTelas() {
        log.info("Listando todas as telas disponíveis");
        return telaPermissaoRepository.findAll().stream()
                .map(this::toTelaPermissaoResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> buscarPermissoesUsuario(Long usuarioId) {
        log.info("Buscando permissões do usuário ID: {}", usuarioId);
        
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + usuarioId);
        }

        return permissaoUsuarioRepository.findByUsuarioId(usuarioId).stream()
                .map(p -> p.getTela().getId())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> buscarMinhasPermissoes(String email) {
        log.info("Buscando permissões do usuário autenticado. Email: {}", email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado. Email: {}", email);
                    return new UsuarioNaoEncontradoException("Usuário não encontrado com email: " + email);
                });

        return permissaoUsuarioRepository.findByUsuarioId(usuario.getId()).stream()
                .map(p -> p.getTela().getId())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PermissaoUsuarioResponse> buscarPermissoesCompletasUsuario(Long usuarioId) {
        log.info("Buscando permissões completas do usuário ID: {}", usuarioId);
        
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + usuarioId);
        }

        return permissaoUsuarioRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toPermissaoUsuarioResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void atualizarPermissoes(Long usuarioId, AtualizarPermissoesRequest request) {
        log.info("Atualizando permissões do usuário ID: {}", usuarioId);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualizar permissões de usuário inexistente. ID: {}", usuarioId);
                    return new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + usuarioId);
                });

        if (usuario.getRole() != Role.USER) {
            log.warn("Tentativa de atualizar permissões de usuário que não é USER. ID: {}, Role: {}", 
                    usuarioId, usuario.getRole());
            throw new IllegalStateException("Apenas usuários comuns podem ter permissões gerenciadas");
        }

        permissaoUsuarioRepository.deleteByUsuarioId(usuarioId);
        log.debug("Permissões antigas removidas para o usuário ID: {}", usuarioId);

        for (String telaId : request.telasPermitidas()) {
            TelaPermissao tela = telaPermissaoRepository.findById(telaId)
                    .orElseThrow(() -> {
                        log.warn("Tela não encontrada. ID: {}", telaId);
                        return new IllegalArgumentException("Tela não encontrada com ID: " + telaId);
                    });

            PermissaoUsuario permissao = PermissaoUsuario.builder()
                    .usuario(usuario)
                    .tela(tela)
                    .build();

            permissaoUsuarioRepository.save(permissao);
            log.debug("Permissão adicionada: Usuário ID: {}, Tela ID: {}", usuarioId, telaId);
        }

        log.info("Permissões atualizadas com sucesso para o usuário ID: {}", usuarioId);
    }

    @Transactional
    public void removerTodasPermissoes(Long usuarioId) {
        log.info("Removendo todas as permissões do usuário ID: {}", usuarioId);
        
        if (!usuarioRepository.existsById(usuarioId)) {
            log.warn("Tentativa de remover permissões de usuário inexistente. ID: {}", usuarioId);
            return; // Não lança exceção, apenas retorna silenciosamente
        }

        permissaoUsuarioRepository.deleteByUsuarioId(usuarioId);
        log.info("Permissões removidas do usuário ID: {}", usuarioId);
    }

    private TelaPermissaoResponse toTelaPermissaoResponse(TelaPermissao tela) {
        return new TelaPermissaoResponse(
                tela.getId(),
                tela.getNome(),
                tela.getRota(),
                tela.getDescricao()
        );
    }

    private PermissaoUsuarioResponse toPermissaoUsuarioResponse(PermissaoUsuario permissao) {
        return new PermissaoUsuarioResponse(
                permissao.getId(),
                permissao.getUsuario().getId(),
                permissao.getTela().getId(),
                toTelaPermissaoResponse(permissao.getTela())
        );
    }
}
