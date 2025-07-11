package com.lojatenis.dto.response;

import com.lojatenis.domain.enums.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO extends RepresentationModel<PedidoResponseDTO> {
    private Long id;
    private LocalDateTime dataPedido;
    private BigDecimal valorTotal;
    private StatusPedido status;
    private UsuarioResponseDTO usuario;
    private List<ItemPedidoResponseDTO> itens;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
