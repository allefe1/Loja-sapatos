package com.lojatenis.service;

import com.lojatenis.domain.ItemPedido;
import com.lojatenis.domain.Pedido;
import com.lojatenis.domain.Tenis;
import com.lojatenis.domain.Usuario;
import com.lojatenis.domain.enums.StatusPedido;
import com.lojatenis.dto.request.ItemPedidoCreateRequestDTO;
import com.lojatenis.dto.request.PedidoCreateRequestDTO;
import com.lojatenis.dto.response.ItemPedidoResponseDTO;
import com.lojatenis.dto.response.PedidoResponseDTO;
import com.lojatenis.dto.response.TenisResponseDTO;
import com.lojatenis.dto.response.UsuarioResponseDTO;
import com.lojatenis.exception.BusinessException;
import com.lojatenis.exception.ResourceNotFoundException;
import com.lojatenis.repository.PedidoRepository;
import com.lojatenis.repository.TenisRepository;
import com.lojatenis.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TenisRepository tenisRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         TenisRepository tenisRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tenisRepository = tenisRepository;
    }

    public Page<PedidoResponseDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os pedidos ativos - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Pedido> pedidoPage = pedidoRepository.findAllActive(pageable);
        return pedidoPage.map(this::toResponseDTO);
    }

    public PedidoResponseDTO findById(Long id) {
        log.info("Buscando pedido por ID: {}", id);

        Pedido pedido = pedidoRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));

        return toResponseDTO(pedido);
    }

    public Page<PedidoResponseDTO> findByUsuarioId(Long usuarioId, Pageable pageable) {
        log.info("Buscando pedidos do usuário ID: {}", usuarioId);

        // Verificar se usuário existe
        if (!usuarioRepository.existsActiveById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }

        Page<Pedido> pedidos = pedidoRepository.findActiveByUsuarioId(usuarioId, pageable);
        return pedidos.map(this::toResponseDTO);
    }

    public Page<PedidoResponseDTO> findByStatus(StatusPedido status, Pageable pageable) {
        log.info("Buscando pedidos com status: {}", status);

        Page<Pedido> pedidos = pedidoRepository.findActiveByStatus(status, pageable);
        return pedidos.map(this::toResponseDTO);
    }

    public PedidoResponseDTO create(PedidoCreateRequestDTO requestDTO) {
        log.info("Criando novo pedido para usuário ID: {}", requestDTO.getUsuarioId());

        // Buscar e validar usuário
        Usuario usuario = usuarioRepository.findActiveById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + requestDTO.getUsuarioId()));

        // Criar pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);

        // Criar itens do pedido
        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemPedidoCreateRequestDTO itemDTO : requestDTO.getItens()) {
            // Buscar e validar tênis
            Tenis tenis = tenisRepository.findActiveById(itemDTO.getTenisId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tênis não encontrado com ID: " + itemDTO.getTenisId()));

            // Criar item do pedido
            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setTenis(tenis);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setTamanho(itemDTO.getTamanho());
            item.setPrecoUnitario(tenis.getPreco());

            itens.add(item);

            // Calcular valor total
            BigDecimal subtotal = tenis.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));
            valorTotal = valorTotal.add(subtotal);

            log.debug("Item adicionado: {} x {} = {}", tenis.getNome(), itemDTO.getQuantidade(), subtotal);
        }

        pedido.setItens(itens);
        pedido.setValorTotal(valorTotal);

        Pedido savedPedido = pedidoRepository.save(pedido);
        log.info("Pedido criado com sucesso - ID: {} | Valor Total: {} | Itens: {}",
                savedPedido.getId(), valorTotal, itens.size());

        return toResponseDTO(savedPedido);
    }

    public PedidoResponseDTO updateStatus(Long id, StatusPedido novoStatus) {
        log.info("Atualizando status do pedido ID: {} para {}", id, novoStatus);

        Pedido pedido = pedidoRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));

        // Validar transição de status
        if (!isValidStatusTransition(pedido.getStatus(), novoStatus)) {
            throw new BusinessException("Transição de status inválida: " + pedido.getStatus() + " -> " + novoStatus);
        }

        pedido.setStatus(novoStatus);
        Pedido updatedPedido = pedidoRepository.save(pedido);

        log.info("Status do pedido ID: {} atualizado para {}", id, novoStatus);
        return toResponseDTO(updatedPedido);
    }

    public void delete(Long id) {
        log.info("Realizando soft delete do pedido ID: {}", id);

        Pedido pedido = pedidoRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));

        // Só permite cancelar pedidos pendentes
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new BusinessException("Só é possível cancelar pedidos com status PENDENTE");
        }

        pedidoRepository.softDelete(id);
        log.info("Soft delete realizado com sucesso para pedido ID: {}", id);
    }

    private boolean isValidStatusTransition(StatusPedido statusAtual, StatusPedido novoStatus) {
        return switch (statusAtual) {
            case PENDENTE -> novoStatus == StatusPedido.CONFIRMADO || novoStatus == StatusPedido.CANCELADO;
            case CONFIRMADO -> novoStatus == StatusPedido.ENVIADO || novoStatus == StatusPedido.CANCELADO;
            case ENVIADO -> novoStatus == StatusPedido.ENTREGUE;
            case ENTREGUE, CANCELADO -> false; // Status finais
        };
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setDataPedido(pedido.getDataPedido());
        dto.setValorTotal(pedido.getValorTotal());
        dto.setStatus(pedido.getStatus());
        dto.setCreatedAt(pedido.getCreatedAt());
        dto.setUpdatedAt(pedido.getUpdatedAt());

        // Converter usuário
        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO();
        usuarioDTO.setId(pedido.getUsuario().getId());
        usuarioDTO.setNome(pedido.getUsuario().getNome());
        usuarioDTO.setEmail(pedido.getUsuario().getEmail());
        dto.setUsuario(usuarioDTO);

        // Converter itens
        List<ItemPedidoResponseDTO> itensDTO = pedido.getItens().stream()
                .map(this::toItemResponseDTO)
                .toList();
        dto.setItens(itensDTO);

        return dto;
    }

    private ItemPedidoResponseDTO toItemResponseDTO(ItemPedido item) {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();
        dto.setId(item.getId());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setTamanho(item.getTamanho());
        dto.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));

        // Converter tênis
        TenisResponseDTO tenisDTO = new TenisResponseDTO();
        tenisDTO.setId(item.getTenis().getId());
        tenisDTO.setNome(item.getTenis().getNome());
        tenisDTO.setMarca(item.getTenis().getMarca());
        tenisDTO.setModelo(item.getTenis().getModelo());
        tenisDTO.setPreco(item.getTenis().getPreco());
        dto.setTenis(tenisDTO);

        return dto;
    }
}
