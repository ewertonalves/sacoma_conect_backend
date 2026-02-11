package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.enums.Role;
import com.adbrassacoma.administrativo.domain.model.PermissaoUsuario;
import com.adbrassacoma.administrativo.domain.model.TelaPermissao;
import com.adbrassacoma.administrativo.domain.model.Usuario;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarPermissoesRequest;
import com.adbrassacoma.administrativo.infrastructure.exception.UsuarioNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.repository.PermissaoUsuarioRepository;
import com.adbrassacoma.administrativo.infrastructure.repository.TelaPermissaoRepository;
import com.adbrassacoma.administrativo.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PermissaoService")
class PermissaoServiceTest {

    @Mock
    private TelaPermissaoRepository telaPermissaoRepository;

    @Mock
    private PermissaoUsuarioRepository permissaoUsuarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PermissaoService permissaoService;

    @Test
    void listarTodasTelasDeveRetornarLista() {
        TelaPermissao tela = TelaPermissao.builder().id("membros").nome("Membros").rota("/membros").build();
        when(telaPermissaoRepository.findAll()).thenReturn(List.of(tela));

        var list = permissaoService.listarTodasTelas();

        assertEquals(1, list.size());
        assertEquals("membros", list.get(0).id());
        assertEquals("Membros", list.get(0).nome());
    }

    @Test
    void buscarPermissoesUsuarioDeveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        assertThrows(UsuarioNaoEncontradoException.class, () -> permissaoService.buscarPermissoesUsuario(999L));
    }

    @Test
    void buscarPermissoesUsuarioDeveRetornarListaDeIds() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        TelaPermissao t = TelaPermissao.builder().id("membros").nome("M").rota("/m").build();
        Usuario u = Usuario.builder().id(1L).email("u@u.com").senha("s").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        PermissaoUsuario p = PermissaoUsuario.builder().id(1L).usuario(u).tela(t).build();
        when(permissaoUsuarioRepository.findByUsuarioId(1L)).thenReturn(List.of(p));

        var ids = permissaoService.buscarPermissoesUsuario(1L);

        assertEquals(List.of("membros"), ids);
    }

    @Test
    void buscarMinhasPermissoesDeveLancarQuandoEmailNaoEncontrado() {
        when(usuarioRepository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> permissaoService.buscarMinhasPermissoes("nao@existe.com"));
    }

    @Test
    void atualizarPermissoesDeveLancarQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> permissaoService.atualizarPermissoes(999L, new AtualizarPermissoesRequest(List.of())));
    }

    @Test
    void atualizarPermissoesDeveLancarQuandoUsuarioNaoEUser() {
        Usuario admin = Usuario.builder().id(1L).nome("A").email("a@a.com").senha("s").role(Role.ADMIN).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(IllegalStateException.class,
                () -> permissaoService.atualizarPermissoes(1L, new AtualizarPermissoesRequest(List.of("membros"))));
    }

    @Test
    void atualizarPermissoesDeveSalvarQuandoUsuarioEUser() {
        Usuario user = Usuario.builder().id(1L).nome("U").email("u@u.com").senha("s").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        TelaPermissao tela = TelaPermissao.builder().id("membros").nome("M").rota("/m").build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));
        when(telaPermissaoRepository.findById("membros")).thenReturn(Optional.of(tela));
        when(permissaoUsuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        permissaoService.atualizarPermissoes(1L, new AtualizarPermissoesRequest(List.of("membros")));

        verify(permissaoUsuarioRepository).deleteByUsuarioId(1L);
        verify(permissaoUsuarioRepository).save(any(PermissaoUsuario.class));
    }

    @Test
    void removerTodasPermissoesNaoDeveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        assertDoesNotThrow(() -> permissaoService.removerTodasPermissoes(999L));
        verify(permissaoUsuarioRepository, never()).deleteByUsuarioId(any());
    }

    @Test
    void removerTodasPermissoesDeveChamarDeleteQuandoUsuarioExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        permissaoService.removerTodasPermissoes(1L);

        verify(permissaoUsuarioRepository).deleteByUsuarioId(1L);
    }

    @Test
    void buscarMinhasPermissoesDeveRetornarListaQuandoEncontrado() {
        Usuario u = Usuario.builder().id(1L).email("u@u.com").senha("s").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        TelaPermissao t = TelaPermissao.builder().id("membros").nome("M").rota("/m").build();
        PermissaoUsuario p = PermissaoUsuario.builder().id(1L).usuario(u).tela(t).build();
        when(usuarioRepository.findByEmail("u@u.com")).thenReturn(Optional.of(u));
        when(permissaoUsuarioRepository.findByUsuarioId(1L)).thenReturn(List.of(p));

        var ids = permissaoService.buscarMinhasPermissoes("u@u.com");

        assertEquals(List.of("membros"), ids);
    }

    @Test
    void buscarPermissoesCompletasUsuarioDeveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        assertThrows(UsuarioNaoEncontradoException.class, () -> permissaoService.buscarPermissoesCompletasUsuario(999L));
    }

    @Test
    void buscarPermissoesCompletasUsuarioDeveRetornarLista() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        TelaPermissao t = TelaPermissao.builder().id("membros").nome("M").rota("/m").build();
        Usuario u = Usuario.builder().id(1L).email("u@u.com").senha("s").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        PermissaoUsuario p = PermissaoUsuario.builder().id(1L).usuario(u).tela(t).build();
        when(permissaoUsuarioRepository.findByUsuarioId(1L)).thenReturn(List.of(p));

        var list = permissaoService.buscarPermissoesCompletasUsuario(1L);

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).usuarioId());
        assertEquals("membros", list.get(0).telaId());
    }

    @Test
    void atualizarPermissoesDeveLancarQuandoTelaNaoEncontrada() {
        Usuario user = Usuario.builder().id(1L).nome("U").email("u@u.com").senha("s").role(Role.USER).dataCriacao(LocalDateTime.now()).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));
        when(telaPermissaoRepository.findById("tela-inexistente")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> permissaoService.atualizarPermissoes(1L, new AtualizarPermissoesRequest(List.of("tela-inexistente"))));
    }
}
