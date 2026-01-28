package com.adbrassacoma.administrativo.infrastructure.repository;

import com.adbrassacoma.administrativo.domain.model.Membros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MembrosRepository extends JpaRepository<Membros, Long> {

    List<Membros> findByNomeContainingIgnoreCase(String nome);

    Optional<Membros> findByCpf(String cpf);

    Optional<Membros> findByRi(String ri);

    Optional<Membros> findByRg(String rg);
    
}
