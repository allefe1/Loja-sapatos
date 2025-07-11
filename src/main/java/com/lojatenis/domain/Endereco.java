package com.lojatenis.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enderecos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Endereco extends BaseEntity {

    @Column(nullable = false)
    private String rua;

    @Column(nullable = false)
    private String numero;

    private String complemento;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false, length = 8)
    private String cep;

    @OneToOne(mappedBy = "endereco")
    private Usuario usuario;
}
