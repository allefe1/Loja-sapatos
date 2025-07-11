package com.lojatenis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenisResponseDTO extends RepresentationModel<TenisResponseDTO> {
    private Long id;
    private String nome;
    private String marca;
    private String modelo;
    private BigDecimal preco;
    private String descricao;
    private String cor;
    private String material;
    private Set<CategoriaResponseDTO> categorias;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
