package com.adbrassacoma.administrativo.infrastructure.repository;

import com.adbrassacoma.administrativo.domain.enums.TipoFinanceiro;
import com.adbrassacoma.administrativo.domain.model.Financeiro;
import com.adbrassacoma.administrativo.domain.model.Membros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceiroRepository extends JpaRepository<Financeiro, Long> {
    
    List<Financeiro> findByTipo(TipoFinanceiro tipo);
    
    List<Financeiro> findByMembro(Membros membro);
    
    List<Financeiro> findByMembroId(Long membroId);
}
