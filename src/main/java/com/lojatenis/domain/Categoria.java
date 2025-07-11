package com.lojatenis.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categorias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Categoria extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String nome;

    private String descricao;

    @ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
    private Set<Tenis> tenis = new HashSet<>();
}
