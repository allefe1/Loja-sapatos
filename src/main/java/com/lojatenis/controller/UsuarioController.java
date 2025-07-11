package com.lojatenis.controller;

import com.lojatenis.dto.request.UsuarioCreateRequestDTO;
import com.lojatenis.dto.response.UsuarioResponseDTO;
import com.lojatenis.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/usuarios")
@Validated
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<UsuarioResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<UsuarioResponseDTO> usuarioPage = usuarioService.findAll(pageable);

        // Adicionar links HATEOAS para cada item
        usuarioPage.getContent().forEach(this::addSelfLink);

        // Criar PagedModel manualmente
        PagedModel<UsuarioResponseDTO> pagedModel = PagedModel.of(
                usuarioPage.getContent(),
                new PagedModel.PageMetadata(
                        usuarioPage.getSize(),
                        usuarioPage.getNumber(),
                        usuarioPage.getTotalElements(),
                        usuarioPage.getTotalPages()
                )
        );

        // Adicionar links de navegação
        pagedModel.add(linkTo(methodOn(UsuarioController.class).findAll(page, size, sort)).withSelfRel());

        if (usuarioPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(UsuarioController.class)
                    .findAll(page + 1, size, sort)).withRel("next"));
        }

        if (usuarioPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(UsuarioController.class)
                    .findAll(page - 1, size, sort)).withRel("prev"));
        }

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.findById(id);
        addSelfLink(usuario);
        addRelatedLinks(usuario);

        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioCreateRequestDTO requestDTO) {
        UsuarioResponseDTO createdUsuario = usuarioService.create(requestDTO);
        addSelfLink(createdUsuario);
        addRelatedLinks(createdUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsuario);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody UsuarioCreateRequestDTO requestDTO) {
        UsuarioResponseDTO updatedUsuario = usuarioService.update(id, requestDTO);
        addSelfLink(updatedUsuario);
        addRelatedLinks(updatedUsuario);

        return ResponseEntity.ok(updatedUsuario);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addSelfLink(UsuarioResponseDTO usuario) {
        usuario.add(linkTo(methodOn(UsuarioController.class).findById(usuario.getId())).withSelfRel());
    }

    private void addRelatedLinks(UsuarioResponseDTO usuario) {
        // Links relacionados seguindo HATEOAS nível 3
        usuario.add(linkTo(methodOn(UsuarioController.class).update(usuario.getId(), null)).withRel("update"));
        usuario.add(linkTo(methodOn(UsuarioController.class).delete(usuario.getId())).withRel("delete"));
        usuario.add(linkTo(methodOn(UsuarioController.class).findAll(0, 10, "id")).withRel("all-usuarios"));

        // Link para pedidos do usuário
        usuario.add(linkTo(methodOn(PedidoController.class)
                .findByUsuario(usuario.getId(), 0, 10, "dataPedido")).withRel("user-orders"));
    }
}
