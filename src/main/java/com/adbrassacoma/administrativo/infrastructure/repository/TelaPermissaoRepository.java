package com.adbrassacoma.administrativo.infrastructure.repository;

import com.adbrassacoma.administrativo.domain.model.TelaPermissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelaPermissaoRepository extends JpaRepository<TelaPermissao, String> {
    Optional<TelaPermissao> findById(String id);
}
