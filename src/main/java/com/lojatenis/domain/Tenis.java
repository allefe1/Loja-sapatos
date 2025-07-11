package com.lojatenis.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tenis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Tenis extends BaseEntity {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String cor;

    private String material;

    // Relacionamento N-N com Categoria
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tenis_categoria",
            joinColumns = @JoinColumn(name = "tenis_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<>();

    // Relacionamento 1-N com ItemPedido
    @OneToMany(mappedBy = "tenis", fetch = FetchType.LAZY)
    private List<ItemPedido> itens = new ArrayList<>();
}
