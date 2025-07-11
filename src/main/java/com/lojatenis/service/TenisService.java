package com.lojatenis.service;

import com.lojatenis.domain.Categoria;
import com.lojatenis.domain.Tenis;
import com.lojatenis.dto.request.TenisCreateRequestDTO;
import com.lojatenis.dto.response.TenisResponseDTO;
import com.lojatenis.exception.ResourceNotFoundException;
import com.lojatenis.repository.CategoriaRepository;
import com.lojatenis.repository.TenisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TenisService {

    private final TenisRepository tenisRepository;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaService categoriaService;

    public TenisService(TenisRepository tenisRepository,
                        CategoriaRepository categoriaRepository,
                        CategoriaService categoriaService) {
        this.tenisRepository = tenisRepository;
        this.categoriaRepository = categoriaRepository;
        this.categoriaService = categoriaService;
    }

    public Page<TenisResponseDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os tênis ativos - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Tenis> tenisPage = tenisRepository.findAllActive(pageable);
        return tenisPage.map(this::toResponseDTO);
    }

    public TenisResponseDTO findById(Long id) {
        log.info("Buscando tênis por ID: {}", id);

        Tenis tenis = tenisRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tênis não encontrado com ID: " + id));

        return toResponseDTO(tenis);
    }

    public TenisResponseDTO create(TenisCreateRequestDTO requestDTO) {
        log.info("Criando novo tênis: {}", requestDTO.getNome());

        // Validar e buscar categorias
        Set<Categoria> categorias = validateAndGetCategorias(requestDTO.getCategoriaIds());

        Tenis tenis = new Tenis();
        tenis.setNome(requestDTO.getNome());
        tenis.setMarca(requestDTO.getMarca());
        tenis.setModelo(requestDTO.getModelo());
        tenis.setPreco(requestDTO.getPreco());
        tenis.setDescricao(requestDTO.getDescricao());
        tenis.setCor(requestDTO.getCor());
        tenis.setMaterial(requestDTO.getMaterial());
        tenis.setCategorias(categorias);

        Tenis savedTenis = tenisRepository.save(tenis);
        log.info("Tênis criado com sucesso - ID: {}", savedTenis.getId());

        return toResponseDTO(savedTenis);
    }

    public TenisResponseDTO update(Long id, TenisCreateRequestDTO requestDTO) {
        log.info("Atualizando tênis ID: {}", id);

        Tenis tenis = tenisRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tênis não encontrado com ID: " + id));

        // Validar e buscar categorias
        Set<Categoria> categorias = validateAndGetCategorias(requestDTO.getCategoriaIds());

        tenis.setNome(requestDTO.getNome());
        tenis.setMarca(requestDTO.getMarca());
        tenis.setModelo(requestDTO.getModelo());
        tenis.setPreco(requestDTO.getPreco());
        tenis.setDescricao(requestDTO.getDescricao());
        tenis.setCor(requestDTO.getCor());
        tenis.setMaterial(requestDTO.getMaterial());
        tenis.setCategorias(categorias);

        Tenis updatedTenis = tenisRepository.save(tenis);
        log.info("Tênis atualizado com sucesso - ID: {}", updatedTenis.getId());

        return toResponseDTO(updatedTenis);
    }

    public void delete(Long id) {
        log.info("Realizando soft delete do tênis ID: {}", id);

        if (!tenisRepository.existsActiveById(id)) {
            throw new ResourceNotFoundException("Tênis não encontrado com ID: " + id);
        }

        tenisRepository.softDelete(id);
        log.info("Soft delete realizado com sucesso para tênis ID: {}", id);
    }

    private Set<Categoria> validateAndGetCategorias(Set<Long> categoriaIds) {
        Set<Categoria> categorias = new HashSet<>();
        for (Long categoriaId : categoriaIds) {
            Categoria categoria = categoriaRepository.findActiveById(categoriaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + categoriaId));
            categorias.add(categoria);
        }
        return categorias;
    }

    private TenisResponseDTO toResponseDTO(Tenis tenis) {
        TenisResponseDTO dto = new TenisResponseDTO();
        dto.setId(tenis.getId());
        dto.setNome(tenis.getNome());
        dto.setMarca(tenis.getMarca());
        dto.setModelo(tenis.getModelo());
        dto.setPreco(tenis.getPreco());
        dto.setDescricao(tenis.getDescricao());
        dto.setCor(tenis.getCor());
        dto.setMaterial(tenis.getMaterial());
        dto.setCreatedAt(tenis.getCreatedAt());
        dto.setUpdatedAt(tenis.getUpdatedAt());

        // Converter categorias
        dto.setCategorias(tenis.getCategorias().stream()
                .map(categoria -> {
                    var categoriaDTO = new com.lojatenis.dto.response.CategoriaResponseDTO();
                    categoriaDTO.setId(categoria.getId());
                    categoriaDTO.setNome(categoria.getNome());
                    categoriaDTO.setDescricao(categoria.getDescricao());
                    return categoriaDTO;
                })
                .collect(Collectors.toSet()));

        return dto;
    }

    public Page<TenisResponseDTO> findByNome(String nome, Pageable pageable) {
        log.info("Buscando tênis por nome: {}", nome);

        Page<Tenis> tenisPage = tenisRepository.findByNomeContainingIgnoreCase(nome, pageable);
        return tenisPage.map(this::toResponseDTO);
    }

    public Page<TenisResponseDTO> findByMarca(String marca, Pageable pageable) {
        log.info("Buscando tênis por marca: {}", marca);

        Page<Tenis> tenisPage = tenisRepository.findByMarcaContainingIgnoreCase(marca, pageable);
        return tenisPage.map(this::toResponseDTO);
    }

    public Page<TenisResponseDTO> findByPrecoRange(BigDecimal precoMin, BigDecimal precoMax, Pageable pageable) {
        log.info("Buscando tênis por faixa de preço: {} - {}", precoMin, precoMax);

        Page<Tenis> tenisPage = tenisRepository.findByPrecoBetween(precoMin, precoMax, pageable);
        return tenisPage.map(this::toResponseDTO);
    }

}
