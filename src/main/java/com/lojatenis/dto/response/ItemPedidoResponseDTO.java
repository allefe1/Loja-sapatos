package com.lojatenis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoResponseDTO {
    private Long id;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private Integer tamanho;
    private BigDecimal subtotal;
    private TenisResponseDTO tenis;
}
