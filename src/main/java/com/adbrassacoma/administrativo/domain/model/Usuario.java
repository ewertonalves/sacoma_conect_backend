package com.adbrassacoma.administrativo.domain.model;

import java.time.LocalDateTime;

import com.adbrassacoma.administrativo.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Column(name = "senha", nullable = false, length = 120)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    private void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        if (this.role == null) {
            this.role = Role.USER;
        }
    }

}
