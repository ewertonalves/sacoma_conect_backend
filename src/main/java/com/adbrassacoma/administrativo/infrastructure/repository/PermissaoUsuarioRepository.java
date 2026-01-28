package com.adbrassacoma.administrativo.infrastructure.repository;

import com.adbrassacoma.administrativo.domain.model.PermissaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissaoUsuarioRepository extends JpaRepository<PermissaoUsuario, Long> {
    
    List<PermissaoUsuario> findByUsuarioId(Long usuarioId);
    
    @Modifying
    @Query("DELETE FROM PermissaoUsuario p WHERE p.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    boolean existsByUsuarioIdAndTelaId(Long usuarioId, String telaId);
}
