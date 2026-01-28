package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.enums.Role;
import com.adbrassacoma.administrativo.domain.model.Usuario;
import com.adbrassacoma.administrativo.infrastructure.config.JwtService;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarUsuarioRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.AuthResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroUsuarioRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.LoginRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.UsuarioResponse;
import com.adbrassacoma.administrativo.infrastructure.exception.CredenciaisInvalidasException;
import com.adbrassacoma.administrativo.infrastructure.exception.EmailJaCadastradoException;
import com.adbrassacoma.administrativo.infrastructure.exception.UsuarioNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.repository.UsuarioRepository;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class AuthService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PermissaoService permissaoService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Lazy AuthenticationManager authenticationManager,
            PermissaoService permissaoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.permissaoService = permissaoService;
    }

    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {
        log.info("Iniciando cadastro de usuário com email: {}", request.email());
        
        if (usuarioRepository.existsByEmail(request.email())) {
            log.warn("Tentativa de cadastro com email já existente: {}", request.email());
            throw new EmailJaCadastradoException("Email já cadastrado no sistema");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .role(Role.USER)
                .build();

        usuario = usuarioRepository.save(usuario);
        log.info("Usuário cadastrado com sucesso. ID: {}, Email: {}", usuario.getId(), usuario.getEmail());

        return toUsuarioResponse(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Tentativa de login para email: {}", request.email());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login falhou: usuário não encontrado com email: {}", request.email());
                    return new CredenciaisInvalidasException("Usuário não encontrado");
                });

        String token = jwtService.generateToken(usuario.getEmail());
        log.info("Login realizado com sucesso. Usuário ID: {}, Email: {}, Role: {}", 
                usuario.getId(), usuario.getEmail(), usuario.getRole());

        return new AuthResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toUsuarioResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> buscarPorNome(String nome) {
        List<Usuario> usuarios = usuarioRepository.buscarPorNome(nome);
        
        if (usuarios.isEmpty()) {
            throw new UsuarioNaoEncontradoException("Nenhum usuário encontrado com nome contendo: " + nome);
        }
        
        return usuarios.stream()
                .map(this::toUsuarioResponse)
                .toList();
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, AtualizarUsuarioRequest request) {
        log.info("Iniciando atualização de usuário. ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualizar usuário inexistente. ID: {}", id);
                    return new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + id);
                });

        if (isAdminMaster(usuario)) {
            log.warn("Tentativa de editar o administrador master. ID: {}", id);
            throw new IllegalStateException("O administrador master não pode ser editado");
        }

        if (!usuario.getEmail().equals(request.email()) && usuarioRepository.existsByEmail(request.email())) {
            log.warn("Tentativa de atualizar email para um já existente. ID: {}, Novo email: {}", id, request.email());
            throw new EmailJaCadastradoException("Email já cadastrado no sistema");
        }

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());

        if (request.senha() != null && !request.senha().isBlank()) {
            log.debug("Senha do usuário ID: {} será atualizada", id);
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }

        usuario = usuarioRepository.save(usuario);
        log.info("Usuário atualizado com sucesso. ID: {}, Email: {}", usuario.getId(), usuario.getEmail());
        
        return toUsuarioResponse(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando exclusão de usuário. ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de deletar usuário inexistente. ID: {}", id);
                    return new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + id);
                });

        if (isAdminMaster(usuario)) {
            log.warn("Tentativa de excluir o administrador master. ID: {}", id);
            throw new IllegalStateException("O administrador master não pode ser excluído");
        }
        
        usuarioRepository.deleteById(id);
        log.info("Usuário deletado com sucesso. ID: {}", id);
    }

    @Transactional
    public UsuarioResponse promoverParaAdmin(Long id) {
        log.info("Iniciando promoção de usuário para admin. ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de promover usuário inexistente. ID: {}", id);
                    return new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + id);
                });

        // Remove todas as permissões antes de promover para admin
        // Admin tem acesso total, então não precisa de permissões específicas
        if (usuario.getRole() == Role.USER) {
            log.info("Removendo permissões do usuário antes de promover para admin. ID: {}", id);
            permissaoService.removerTodasPermissoes(id);
        }

        usuario.setRole(Role.ADMIN);
        usuario = usuarioRepository.save(usuario);
        log.info("Usuário promovido a admin com sucesso. ID: {}, Email: {}", usuario.getId(), usuario.getEmail());
        
        return toUsuarioResponse(usuario);
    }

    @Transactional
    public UsuarioResponse rebaixarParaUser(Long id) {
        log.info("Iniciando rebaixamento de admin para usuário comum. ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de rebaixar usuário inexistente. ID: {}", id);
                    return new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + id);
                });

        if (isAdminMaster(usuario)) {
            log.warn("Tentativa de rebaixar o administrador master. ID: {}", id);
            throw new IllegalStateException("O administrador master não pode ser rebaixado");
        }

        if (usuario.getRole() != Role.ADMIN) {
            log.warn("Tentativa de rebaixar usuário que não é admin. ID: {}, Role: {}", id, usuario.getRole());
            throw new IllegalStateException("Apenas administradores podem ser rebaixados");
        }

        usuario.setRole(Role.USER);
        usuario = usuarioRepository.save(usuario);
        log.info("Admin rebaixado a usuário comum com sucesso. ID: {}, Email: {}", usuario.getId(), usuario.getEmail());
        
        return toUsuarioResponse(usuario);
    }

    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getDataCriacao()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Carregando detalhes do usuário para autenticação. Email: {}", email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado para autenticação. Email: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado com email: " + email);
                });

        String authority = usuario.getRole() == Role.ADMIN ? "ROLE_ADMIN" : "ROLE_USER";
        log.debug("Usuário carregado com sucesso. Email: {}, Role: {}", email, usuario.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(authority)
                .build();
    }

    /**
     * Verifica se o usuário é o administrador master (não pode ser editado ou excluído)
     * Identifica pelo nome contendo "Administrador Master" ou sendo o primeiro admin criado (ID = 1)
     */
    private boolean isAdminMaster(Usuario usuario) {
        return (usuario.getNome() != null && usuario.getNome().toLowerCase().contains("administrador master")) ||
               (usuario.getId() != null && usuario.getId() == 1L && usuario.getRole() == Role.ADMIN);
    }
}

