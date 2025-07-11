package com.lojatenis.service;

import com.lojatenis.domain.Categoria;
import com.lojatenis.dto.request.CategoriaCreateRequestDTO;
import com.lojatenis.dto.response.CategoriaResponseDTO;
import com.lojatenis.exception.BusinessException;
import com.lojatenis.exception.ResourceNotFoundException;
import com.lojatenis.repository.CategoriaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Page<CategoriaResponseDTO> findAll(Pageable pageable) {
        log.info("Buscando todas as categorias ativas - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Categoria> categoriaPage = categoriaRepository.findAllActive(pageable);
        return categoriaPage.map(this::toResponseDTO);
    }

    public CategoriaResponseDTO findById(Long id) {
        log.info("Buscando categoria por ID: {}", id);

        Categoria categoria = categoriaRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));

        return toResponseDTO(categoria);
    }

    public CategoriaResponseDTO create(CategoriaCreateRequestDTO requestDTO) {
        log.info("Criando nova categoria: {}", requestDTO.getNome());

        // Verificar se já existe categoria com o mesmo nome
        if (categoriaRepository.findByNomeContainingIgnoreCase(requestDTO.getNome(), Pageable.unpaged()).hasContent()) {
            throw new BusinessException("Já existe uma categoria com o nome: " + requestDTO.getNome());
        }

        Categoria categoria = new Categoria();
        categoria.setNome(requestDTO.getNome());
        categoria.setDescricao(requestDTO.getDescricao());

        Categoria savedCategoria = categoriaRepository.save(categoria);
        log.info("Categoria criada com sucesso - ID: {}", savedCategoria.getId());

        return toResponseDTO(savedCategoria);
    }

    public CategoriaResponseDTO update(Long id, CategoriaCreateRequestDTO requestDTO) {
        log.info("Atualizando categoria ID: {}", id);

        Categoria categoria = categoriaRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));

        categoria.setNome(requestDTO.getNome());
        categoria.setDescricao(requestDTO.getDescricao());

        Categoria updatedCategoria = categoriaRepository.save(categoria);
        log.info("Categoria atualizada com sucesso - ID: {}", updatedCategoria.getId());

        return toResponseDTO(updatedCategoria);
    }

    public void delete(Long id) {
        log.info("Realizando soft delete da categoria ID: {}", id);

        if (!categoriaRepository.existsActiveById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada com ID: " + id);
        }

        categoriaRepository.softDelete(id);
        log.info("Soft delete realizado com sucesso para categoria ID: {}", id);
    }

    private CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setDescricao(categoria.getDescricao());
        dto.setCreatedAt(categoria.getCreatedAt());
        dto.setUpdatedAt(categoria.getUpdatedAt());
        return dto;
    }
}
