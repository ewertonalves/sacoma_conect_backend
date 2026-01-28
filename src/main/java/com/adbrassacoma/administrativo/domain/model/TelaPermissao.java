package com.adbrassacoma.administrativo.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "tela_permissao")
public class TelaPermissao {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "rota", nullable = false, length = 200)
    private String rota;

    @Column(name = "descricao", length = 500)
    private String descricao;
}
