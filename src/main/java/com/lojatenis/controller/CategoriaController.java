package com.lojatenis.controller;

import com.lojatenis.dto.request.CategoriaCreateRequestDTO;
import com.lojatenis.dto.response.CategoriaResponseDTO;
import com.lojatenis.service.CategoriaService;
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
@RequestMapping("/categorias")
@Validated
@Slf4j
public class CategoriaController {

    private final CategoriaService categoriaService;

    // REMOVER o PagedResourcesAssembler do construtor
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<CategoriaResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<CategoriaResponseDTO> categoriaPage = categoriaService.findAll(pageable);

        // Adicionar links HATEOAS para cada item
        categoriaPage.getContent().forEach(this::addSelfLink);

        // Criar PagedModel manualmente
        PagedModel<CategoriaResponseDTO> pagedModel = PagedModel.of(
                categoriaPage.getContent(),
                new PagedModel.PageMetadata(
                        categoriaPage.getSize(),
                        categoriaPage.getNumber(),
                        categoriaPage.getTotalElements(),
                        categoriaPage.getTotalPages()
                )
        );

        // Adicionar links de navegação
        pagedModel.add(linkTo(methodOn(CategoriaController.class).findAll(page, size, sort)).withSelfRel());

        if (categoriaPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(CategoriaController.class)
                    .findAll(page + 1, size, sort)).withRel("next"));
        }

        if (categoriaPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(CategoriaController.class)
                    .findAll(page - 1, size, sort)).withRel("prev"));
        }

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable Long id) {
        CategoriaResponseDTO categoria = categoriaService.findById(id);
        addSelfLink(categoria);
        addRelatedLinks(categoria);

        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> create(@Valid @RequestBody CategoriaCreateRequestDTO requestDTO) {
        CategoriaResponseDTO createdCategoria = categoriaService.create(requestDTO);
        addSelfLink(createdCategoria);
        addRelatedLinks(createdCategoria);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategoria);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> update(@PathVariable Long id,
                                                       @Valid @RequestBody CategoriaCreateRequestDTO requestDTO) {
        CategoriaResponseDTO updatedCategoria = categoriaService.update(id, requestDTO);
        addSelfLink(updatedCategoria);
        addRelatedLinks(updatedCategoria);

        return ResponseEntity.ok(updatedCategoria);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addSelfLink(CategoriaResponseDTO categoria) {
        categoria.add(linkTo(methodOn(CategoriaController.class).findById(categoria.getId())).withSelfRel());
    }

    private void addRelatedLinks(CategoriaResponseDTO categoria) {
        categoria.add(linkTo(methodOn(CategoriaController.class).update(categoria.getId(), null)).withRel("update"));
        categoria.add(linkTo(methodOn(CategoriaController.class).delete(categoria.getId())).withRel("delete"));
        categoria.add(linkTo(methodOn(CategoriaController.class).findAll(0, 10, "id")).withRel("all-categorias"));
    }
}
