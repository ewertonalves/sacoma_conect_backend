package com.adbrassacoma.administrativo.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adbrassacoma.administrativo.domain.model.Endereco;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
