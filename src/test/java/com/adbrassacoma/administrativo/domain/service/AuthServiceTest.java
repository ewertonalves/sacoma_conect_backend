package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.enums.Role;
import com.adbrassacoma.administrativo.domain.model.Usuario;
import com.adbrassacoma.administrativo.infrastructure.config.JwtService;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarUsuarioRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroUsuarioRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.LoginRequest;
import com.adbrassacoma.administrativo.infrastructure.exception.CredenciaisInvalidasException;
import com.adbrassacoma.administrativo.infrastructure.exception.EmailJaCadastradoException;
import com.adbrassacoma.administrativo.infrastructure.exception.UsuarioNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PermissaoService permissaoService;

    @InjectMocks
    private AuthService authService;

    @Test
    void cadastrarDeveLancarQuandoEmailJaExiste() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest("Nome", "email@teste.com", "senha123");
        when(usuarioRepository.existsByEmail("email@teste.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> authService.cadastrar(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void cadastrarDeveSalvarERetornarResponse() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest("João", "joao@teste.com", "senha123");
        when(usuarioRepository.existsByEmail("joao@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("encoded");
        Usuario salvo = Usuario.builder().id(1L).nome("João").email("joao@teste.com").senha("encoded").role(Role.USER)
                .dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.save(any())).thenReturn(salvo);

        var response = authService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("João", response.nome());
        assertEquals("joao@teste.com", response.email());
        assertEquals(Role.USER, response.role());
        verify(usuarioRepository).save(any());
    }

    @Test
    void loginDeveLancarQuandoUsuarioNaoEncontradoAposAuth() {
        LoginRequest request = new LoginRequest("user@teste.com", "senha");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(usuarioRepository.findByEmail("user@teste.com")).thenReturn(Optional.empty());

        assertThrows(CredenciaisInvalidasException.class, () -> authService.login(request));
    }

    @Test
    void loginDeveRetornarAuthResponseQuandoSucesso() {
        LoginRequest request = new LoginRequest("user@teste.com", "senha");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        Usuario usuario = Usuario.builder().id(1L).nome("User").email("user@teste.com").senha("encoded").role(Role.USER)
                .dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findByEmail("user@teste.com")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken("user@teste.com")).thenReturn("token-jwt");

        var response = authService.login(request);

        assertNotNull(response);
        assertEquals("token-jwt", response.token());
        assertEquals(1L, response.usuarioId());
        assertEquals("user@teste.com", response.email());
    }

    @Test
    void listarTodosDeveRetornarLista() {
        Usuario u = Usuario.builder().id(1L).nome("A").email("a@a.com").senha("s").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findAll()).thenReturn(List.of(u));

        var list = authService.listarTodos();

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).id());
    }

    @Test
    void buscarPorNomeDeveLancarQuandoNenhumEncontrado() {
        when(usuarioRepository.buscarPorNome("X")).thenReturn(List.of());

        assertThrows(UsuarioNaoEncontradoException.class, () -> authService.buscarPorNome("X"));
    }

    @Test
    void atualizarDeveLancarQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());
        AtualizarUsuarioRequest req = new AtualizarUsuarioRequest("Nome", "email@e.com", null);

        assertThrows(UsuarioNaoEncontradoException.class, () -> authService.atualizar(999L, req));
    }

    @Test
    void atualizarDeveLancarQuandoTentaEditarAdminMaster() {
        Usuario admin = Usuario.builder().id(1L).nome("Administrador Master").email("admin@adm.com").senha("s").role(Role.ADMIN).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        AtualizarUsuarioRequest req = new AtualizarUsuarioRequest("Outro Nome", "admin@adm.com", null);

        assertThrows(IllegalStateException.class, () -> authService.atualizar(1L, req));
    }

    @Test
    void deletarDeveLancarQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> authService.deletar(999L));
    }

    @Test
    void loadUserByUsernameDeveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername("nao@existe.com"));
    }

    @Test
    void loadUserByUsernameDeveRetornarUserDetailsQuandoExiste() {
        Usuario u = Usuario.builder().id(1L).nome("U").email("u@u.com").senha("encoded").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findByEmail("u@u.com")).thenReturn(Optional.of(u));

        var userDetails = authService.loadUserByUsername("u@u.com");

        assertNotNull(userDetails);
        assertEquals("u@u.com", userDetails.getUsername());
        assertEquals("encoded", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void promoverParaAdminDeveLancarQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> authService.promoverParaAdmin(999L));
    }

    @Test
    void rebaixarParaUserDeveLancarQuandoAdminMaster() {
        Usuario admin = Usuario.builder().id(1L).nome("Administrador Master").email("a@a.com").senha("s").role(Role.ADMIN).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(IllegalStateException.class, () -> authService.rebaixarParaUser(1L));
    }

    @Test
    void buscarPorNomeDeveRetornarListaQuandoEncontrado() {
        Usuario u = Usuario.builder().id(1L).nome("Maria").email("maria@teste.com").senha("s").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.buscarPorNome("Maria")).thenReturn(List.of(u));

        var list = authService.buscarPorNome("Maria");

        assertEquals(1, list.size());
        assertEquals("Maria", list.get(0).nome());
    }

    @Test
    void atualizarDeveLancarQuandoEmailJaExisteParaOutroUsuario() {
        Usuario usuario = Usuario.builder().id(2L).nome("User").email("user@teste.com").senha("s").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("outro@teste.com")).thenReturn(true);
        AtualizarUsuarioRequest req = new AtualizarUsuarioRequest("User", "outro@teste.com", null);

        assertThrows(EmailJaCadastradoException.class, () -> authService.atualizar(2L, req));
    }

    @Test
    void atualizarDeveAtualizarSenhaQuandoInformada() {
        Usuario usuario = Usuario.builder().id(2L).nome("User").email("user@teste.com").senha("old").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha")).thenReturn("encodedNova");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        AtualizarUsuarioRequest req = new AtualizarUsuarioRequest("User", "user@teste.com", "novaSenha");

        var response = authService.atualizar(2L, req);

        assertNotNull(response);
        verify(usuarioRepository).save(any());
    }

    @Test
    void deletarDeveLancarQuandoTentaExcluirAdminMaster() {
        Usuario admin = Usuario.builder().id(1L).nome("Administrador Master").email("a@a.com").senha("s").role(Role.ADMIN).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(IllegalStateException.class, () -> authService.deletar(1L));
        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    void promoverParaAdminDeveChamarRemoverPermissoesQuandoUsuarioEUser() {
        Usuario user = Usuario.builder().id(2L).nome("U").email("u@u.com").senha("s").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(user));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        authService.promoverParaAdmin(2L);

        verify(permissaoService).removerTodasPermissoes(2L);
        verify(usuarioRepository).save(any());
    }

    @Test
    void rebaixarParaUserDeveLancarQuandoUsuarioNaoEAdmin() {
        Usuario user = Usuario.builder().id(2L).nome("U").email("u@u.com").senha("s").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> authService.rebaixarParaUser(2L));
    }

    @Test
    void rebaixarParaUserDeveRebaixarComSucesso() {
        Usuario admin = Usuario.builder().id(2L).nome("Admin").email("admin@a.com").senha("s").role(Role.ADMIN).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = authService.rebaixarParaUser(2L);

        assertNotNull(response);
        assertEquals(Role.USER, response.role());
        verify(usuarioRepository).save(any());
    }

    @Test
    void loadUserByUsernameDeveRetornarRoleAdminQuandoUsuarioEAdmin() {
        Usuario admin = Usuario.builder().id(1L).nome("A").email("admin@a.com").senha("encoded").role(Role.ADMIN).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findByEmail("admin@a.com")).thenReturn(Optional.of(admin));

        var userDetails = authService.loadUserByUsername("admin@a.com");

        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void atualizarDeveLancarQuandoAdminMasterPorIdUm() {
        Usuario admin = Usuario.builder().id(1L).nome("Admin").email("a@a.com").senha("s").role(Role.ADMIN).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        AtualizarUsuarioRequest req = new AtualizarUsuarioRequest("Nome", "a@a.com", null);

        assertThrows(IllegalStateException.class, () -> authService.atualizar(1L, req));
    }
}
