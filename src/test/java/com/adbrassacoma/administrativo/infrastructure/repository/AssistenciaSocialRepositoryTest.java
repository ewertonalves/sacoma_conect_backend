package com.adbrassacoma.administrativo.infrastructure.repository;

import com.adbrassacoma.administrativo.domain.model.AssistenciaSocial;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("AssistenciaSocialRepository")
class AssistenciaSocialRepositoryTest {

    @Autowired
    private AssistenciaSocialRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void saveDevePersistirEntidade() {
        AssistenciaSocial a = AssistenciaSocial.builder()
                .nomeAlimento("Arroz")
                .quantidade(new BigDecimal("50"))
                .dataValidade(LocalDate.now().plusMonths(1))
                .familiaBeneficiada("Família X")
                .dataRegistro(LocalDateTime.now())
                .build();
        AssistenciaSocial saved = repository.save(a);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(saved.getId());
        AssistenciaSocial found = repository.findById(saved.getId()).orElseThrow();
        assertEquals("Arroz", found.getNomeAlimento());
    }

    @Test
    void buscarComFiltroDeveRetornarPaginaComResultados() {
        AssistenciaSocial a = AssistenciaSocial.builder()
                .nomeAlimento("Feijão")
                .quantidade(BigDecimal.ONE)
                .dataValidade(LocalDate.now())
                .dataRegistro(LocalDateTime.now())
                .build();
        entityManager.persist(a);
        entityManager.flush();

        var page = repository.buscarComFiltro("Feijão", PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Feijão", page.getContent().get(0).getNomeAlimento());
    }
}
