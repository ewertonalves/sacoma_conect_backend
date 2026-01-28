package com.adbrassacoma.administrativo.infrastructure.repository;

import com.adbrassacoma.administrativo.domain.model.AssistenciaSocial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssistenciaSocialRepository extends JpaRepository<AssistenciaSocial, Long> {
    
    @Query("SELECT a FROM AssistenciaSocial a WHERE " +
           "LOWER(a.nomeAlimento) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(COALESCE(a.familiaBeneficiada, '')) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<AssistenciaSocial> buscarComFiltro(@Param("search") String search, Pageable pageable);
    
    Page<AssistenciaSocial> findByNomeAlimentoContainingIgnoreCase(String nomeAlimento, Pageable pageable);
    
    Page<AssistenciaSocial> findByFamiliaBeneficiadaContainingIgnoreCase(String familiaBeneficiada, Pageable pageable);
}
