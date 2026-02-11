package com.adbrassacoma.administrativo.infrastructure.repository;

import com.adbrassacoma.administrativo.domain.enums.Role;
import com.adbrassacoma.administrativo.domain.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("UsuarioRepository")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmailDeveRetornarUsuarioQuandoExiste() {
        Usuario usuario = Usuario.builder().nome("Teste").email("teste@repo.com").senha("encoded").role(Role.USER).build();
        entityManager.persist(usuario);
        entityManager.flush();

        Optional<Usuario> found = usuarioRepository.findByEmail("teste@repo.com");

        assertTrue(found.isPresent());
        assertEquals("teste@repo.com", found.get().getEmail());
    }

    @Test
    void findByEmailDeveRetornarVazioQuandoNaoExiste() {
        Optional<Usuario> found = usuarioRepository.findByEmail("nao@existe.com");
        assertTrue(found.isEmpty());
    }

    @Test
    void existsByEmailDeveRetornarTrueQuandoExiste() {
        Usuario usuario = Usuario.builder().nome("A").email("a@repo.com").senha("s").role(Role.USER).build();
        entityManager.persist(usuario);
        entityManager.flush();

        assertTrue(usuarioRepository.existsByEmail("a@repo.com"));
    }

    @Test
    void existsByEmailDeveRetornarFalseQuandoNaoExiste() {
        assertFalse(usuarioRepository.existsByEmail("nao@repo.com"));
    }

    @Test
    void buscarPorNomeDeveRetornarUsuariosQueContemNome() {
        Usuario u1 = Usuario.builder().nome("João Silva").email("joao@r.com").senha("s").role(Role.USER).build();
        Usuario u2 = Usuario.builder().nome("Maria Silva").email("maria@r.com").senha("s").role(Role.USER).build();
        entityManager.persist(u1);
        entityManager.persist(u2);
        entityManager.flush();

        List<Usuario> result = usuarioRepository.buscarPorNome("Silva");

        assertEquals(2, result.size());
    }
}
