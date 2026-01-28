package com.adbrassacoma.administrativo.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "assistencia_social")
public class AssistenciaSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nomeAlimento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @Column(length = 255)
    private String familiaBeneficiada;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantidadeCestasBasicas;

    @Column
    private LocalDate dataEntregaCesta;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataRegistro;

    @PrePersist
    private void prePersist() {
        this.dataRegistro = LocalDateTime.now();
    }
}
