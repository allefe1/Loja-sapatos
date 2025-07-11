package com.lojatenis.controller;

import com.lojatenis.domain.enums.StatusPedido;
import com.lojatenis.dto.request.PedidoCreateRequestDTO;
import com.lojatenis.dto.response.PedidoResponseDTO;
import com.lojatenis.service.PedidoService;
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
@RequestMapping("/pedidos")
@Validated
@Slf4j
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<PedidoResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataPedido") String sort,
            @RequestParam(required = false) StatusPedido status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        Page<PedidoResponseDTO> pedidoPage;

        if (status != null) {
            pedidoPage = pedidoService.findByStatus(status, pageable);
        } else {
            pedidoPage = pedidoService.findAll(pageable);
        }

        // Adicionar links HATEOAS para cada item
        pedidoPage.getContent().forEach(this::addSelfLink);

        // Criar PagedModel manualmente
        PagedModel<PedidoResponseDTO> pagedModel = PagedModel.of(
                pedidoPage.getContent(),
                new PagedModel.PageMetadata(
                        pedidoPage.getSize(),
                        pedidoPage.getNumber(),
                        pedidoPage.getTotalElements(),
                        pedidoPage.getTotalPages()
                )
        );

        // Adicionar links de navegação
        pagedModel.add(linkTo(methodOn(PedidoController.class)
                .findAll(page, size, sort, status)).withSelfRel());

        if (pedidoPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(PedidoController.class)
                    .findAll(page + 1, size, sort, status)).withRel("next"));
        }

        if (pedidoPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(PedidoController.class)
                    .findAll(page - 1, size, sort, status)).withRel("prev"));
        }

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @pedidoService.isOwner(#id, authentication.name))")
    public ResponseEntity<PedidoResponseDTO> findById(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.findById(id);
        addSelfLink(pedido);
        addRelatedLinks(pedido);

        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #usuarioId == authentication.principal.id)")
    public ResponseEntity<PagedModel<PedidoResponseDTO>> findByUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataPedido") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        Page<PedidoResponseDTO> pedidos = pedidoService.findByUsuarioId(usuarioId, pageable);

        // Adicionar links HATEOAS
        pedidos.getContent().forEach(this::addSelfLink);

        PagedModel<PedidoResponseDTO> pagedModel = PagedModel.of(
                pedidos.getContent(),
                new PagedModel.PageMetadata(
                        pedidos.getSize(),
                        pedidos.getNumber(),
                        pedidos.getTotalElements(),
                        pedidos.getTotalPages()
                )
        );

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> create(@Valid @RequestBody PedidoCreateRequestDTO requestDTO) {
        PedidoResponseDTO createdPedido = pedidoService.create(requestDTO);
        addSelfLink(createdPedido);
        addRelatedLinks(createdPedido);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPedido);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> updateStatus(@PathVariable Long id,
                                                          @RequestParam StatusPedido status) {
        PedidoResponseDTO updatedPedido = pedidoService.updateStatus(id, status);
        addSelfLink(updatedPedido);
        addRelatedLinks(updatedPedido);

        return ResponseEntity.ok(updatedPedido);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @pedidoService.isOwner(#id, authentication.name))")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addSelfLink(PedidoResponseDTO pedido) {
        pedido.add(linkTo(methodOn(PedidoController.class).findById(pedido.getId())).withSelfRel());
    }

    private void addRelatedLinks(PedidoResponseDTO pedido) {
        // Links relacionados seguindo HATEOAS nível 3
        pedido.add(linkTo(methodOn(PedidoController.class)
                .updateStatus(pedido.getId(), null)).withRel("update-status"));
        pedido.add(linkTo(methodOn(PedidoController.class)
                .delete(pedido.getId())).withRel("cancel"));
        pedido.add(linkTo(methodOn(PedidoController.class)
                .findAll(0, 10, "dataPedido", null)).withRel("all-pedidos"));

        // Links para usuário
        if (pedido.getUsuario() != null) {
            pedido.add(linkTo(methodOn(PedidoController.class)
                    .findByUsuario(pedido.getUsuario().getId(), 0, 10, "dataPedido")).withRel("user-orders"));
        }
    }
}
