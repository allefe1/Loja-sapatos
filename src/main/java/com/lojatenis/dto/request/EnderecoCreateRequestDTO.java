package com.lojatenis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoCreateRequestDTO {

    @NotBlank(message = "Rua é obrigatória")
    @Size(min = 5, max = 100, message = "Rua deve ter entre 5 e 100 caracteres")
    private String rua;

    @NotBlank(message = "Número é obrigatório")
    @Size(min = 1, max = 10, message = "Número deve ter entre 1 e 10 caracteres")
    private String numero;

    @Size(max = 50, message = "Complemento deve ter no máximo 50 caracteres")
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(min = 2, max = 50, message = "Bairro deve ter entre 2 e 50 caracteres")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(min = 2, max = 50, message = "Cidade deve ter entre 2 e 50 caracteres")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{8}$", message = "CEP deve ter 8 dígitos")
    private String cep;
}
