package com.adbrassacoma.administrativo.domain.model;

import com.adbrassacoma.administrativo.domain.enums.CategoriaFinanceira;
import com.adbrassacoma.administrativo.domain.enums.TipoMovimentacaoFinanceira;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "financeiro")
public class Financeiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer codigoFinanceiro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 20)
    private TipoMovimentacaoFinanceira tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CategoriaFinanceira categoria;

    @Column(precision = 15, scale = 2)
    private BigDecimal entrada;

    @Column(precision = 15, scale = 2)
    private BigDecimal saida;

    @Column(length = 255)
    private String observacao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membro_id")
    private Membros membro;

    @PrePersist
    private void prePersist() {
        this.dataRegistro = LocalDateTime.now();
    }
}
