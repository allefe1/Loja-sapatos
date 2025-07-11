package com.lojatenis.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ItemPedido extends BaseEntity {

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private Integer tamanho;

    // Relacionamento N-1 com Pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // Relacionamento N-1 com Tenis
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenis_id", nullable = false)
    private Tenis tenis;
}
