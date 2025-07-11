package com.lojatenis.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoCreateRequestDTO {

    @NotNull(message = "ID do tênis é obrigatório")
    private Long tenisId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    @Max(value = 10, message = "Quantidade máxima é 10 por item")
    private Integer quantidade;

    @NotNull(message = "Tamanho é obrigatório")
    @Min(value = 30, message = "Tamanho mínimo é 30")
    @Max(value = 50, message = "Tamanho máximo é 50")
    private Integer tamanho;
}
