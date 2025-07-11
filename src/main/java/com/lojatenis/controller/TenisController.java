package com.lojatenis.controller;

import com.lojatenis.dto.request.TenisCreateRequestDTO;
import com.lojatenis.dto.response.TenisResponseDTO;
import com.lojatenis.service.TenisService;
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

import java.math.BigDecimal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/tenis")
@Validated
@Slf4j
public class TenisController {

    private final TenisService tenisService;

    public TenisController(TenisService tenisService) {
        this.tenisService = tenisService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<TenisResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<TenisResponseDTO> tenisPage;

        // Aplicar filtros se fornecidos
        if (nome != null && !nome.trim().isEmpty()) {
            tenisPage = tenisService.findByNome(nome, pageable);
        } else if (marca != null && !marca.trim().isEmpty()) {
            tenisPage = tenisService.findByMarca(marca, pageable);
        } else if (precoMin != null && precoMax != null) {
            tenisPage = tenisService.findByPrecoRange(precoMin, precoMax, pageable);
        } else {
            tenisPage = tenisService.findAll(pageable);
        }

        // Adicionar links HATEOAS para cada item
        tenisPage.getContent().forEach(this::addSelfLink);

        // Criar PagedModel manualmente
        PagedModel<TenisResponseDTO> pagedModel = PagedModel.of(
                tenisPage.getContent(),
                new PagedModel.PageMetadata(
                        tenisPage.getSize(),
                        tenisPage.getNumber(),
                        tenisPage.getTotalElements(),
                        tenisPage.getTotalPages()
                )
        );

        // Adicionar links de navegação
        pagedModel.add(linkTo(methodOn(TenisController.class)
                .findAll(page, size, sort, nome, marca, precoMin, precoMax)).withSelfRel());

        if (tenisPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(TenisController.class)
                    .findAll(page + 1, size, sort, nome, marca, precoMin, precoMax)).withRel("next"));
        }

        if (tenisPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(TenisController.class)
                    .findAll(page - 1, size, sort, nome, marca, precoMin, precoMax)).withRel("prev"));
        }

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenisResponseDTO> findById(@PathVariable Long id) {
        TenisResponseDTO tenis = tenisService.findById(id);
        addSelfLink(tenis);
        addRelatedLinks(tenis);

        return ResponseEntity.ok(tenis);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenisResponseDTO> create(@Valid @RequestBody TenisCreateRequestDTO requestDTO) {
        TenisResponseDTO createdTenis = tenisService.create(requestDTO);
        addSelfLink(createdTenis);
        addRelatedLinks(createdTenis);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTenis);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenisResponseDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody TenisCreateRequestDTO requestDTO) {
        TenisResponseDTO updatedTenis = tenisService.update(id, requestDTO);
        addSelfLink(updatedTenis);
        addRelatedLinks(updatedTenis);

        return ResponseEntity.ok(updatedTenis);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tenisService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addSelfLink(TenisResponseDTO tenis) {
        tenis.add(linkTo(methodOn(TenisController.class).findById(tenis.getId())).withSelfRel());
    }

    private void addRelatedLinks(TenisResponseDTO tenis) {
        tenis.add(linkTo(methodOn(TenisController.class).update(tenis.getId(), null)).withRel("update"));
        tenis.add(linkTo(methodOn(TenisController.class).delete(tenis.getId())).withRel("delete"));
        tenis.add(linkTo(methodOn(TenisController.class)
                .findAll(0, 10, "id", null, null, null, null)).withRel("all-tenis"));

        // Links para categorias relacionadas
        tenis.getCategorias().forEach(categoria -> {
            categoria.add(linkTo(methodOn(CategoriaController.class)
                    .findById(categoria.getId())).withRel("categoria"));
        });
    }
}
