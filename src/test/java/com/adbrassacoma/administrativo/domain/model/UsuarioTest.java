package com.adbrassacoma.administrativo.domain.model;

import com.adbrassacoma.administrativo.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Usuario")
class UsuarioTest {

    @Test
    void builderDeveCriarUsuarioComTodosCampos() {
        LocalDateTime dataCriacao = LocalDateTime.now().minusDays(1);
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .senha("encoded")
                .role(Role.USER)
                .dataCriacao(dataCriacao)
                .build();

        assertEquals(1L, usuario.getId());
        assertEquals("João Silva", usuario.getNome());
        assertEquals("joao@email.com", usuario.getEmail());
        assertEquals("encoded", usuario.getSenha());
        assertEquals(Role.USER, usuario.getRole());
        assertEquals(dataCriacao, usuario.getDataCriacao());
    }

    @Test
    void builderDefaultRoleDeveSerUser() {
        Usuario usuario = Usuario.builder()
                .nome("Teste")
                .email("teste@email.com")
                .senha("senha")
                .build();
        assertEquals(Role.USER, usuario.getRole());
    }

    @Test
    void settersDevemFuncionar() {
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNome("Maria");
        usuario.setEmail("maria@email.com");
        usuario.setSenha("hash");
        usuario.setRole(Role.ADMIN);
        usuario.setDataCriacao(LocalDateTime.now());

        assertEquals(2L, usuario.getId());
        assertEquals("Maria", usuario.getNome());
        assertEquals(Role.ADMIN, usuario.getRole());
    }
}
