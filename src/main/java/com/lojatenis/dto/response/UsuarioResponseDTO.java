package com.lojatenis.dto.response;

import com.lojatenis.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO extends RepresentationModel<UsuarioResponseDTO> {
    private Long id;
    private String email;
    private String nome;
    private String telefone;
    private Role role;
    private EnderecoResponseDTO endereco;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
