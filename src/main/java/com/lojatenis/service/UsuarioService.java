package com.lojatenis.service;

import com.lojatenis.domain.Usuario;
import com.lojatenis.dto.request.UsuarioCreateRequestDTO;
import com.lojatenis.dto.response.UsuarioResponseDTO;
import com.lojatenis.exception.BusinessException;
import com.lojatenis.exception.ResourceNotFoundException;
import com.lojatenis.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // MÉTODO NOVO - Listar todos os usuários
    public Page<UsuarioResponseDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os usuários ativos - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Usuario> usuarioPage = usuarioRepository.findAllActive(pageable);
        return usuarioPage.map(this::toResponseDTO);
    }

    // MÉTODO NOVO - Buscar usuário por ID
    public UsuarioResponseDTO findById(Long id) {
        log.info("Buscando usuário por ID: {}", id);

        Usuario usuario = usuarioRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        return toResponseDTO(usuario);
    }

    // MÉTODO NOVO - Atualizar usuário
    public UsuarioResponseDTO update(Long id, UsuarioCreateRequestDTO requestDTO) {
        log.info("Atualizando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        // Verificar se email já existe (se foi alterado)
        if (!usuario.getEmail().equals(requestDTO.getEmail()) &&
                usuarioRepository.existsActiveByEmail(requestDTO.getEmail())) {
            throw new BusinessException("Email já está em uso: " + requestDTO.getEmail());
        }

        // Atualizar dados
        usuario.setEmail(requestDTO.getEmail());
        usuario.setNome(requestDTO.getNome());
        usuario.setTelefone(requestDTO.getTelefone());
        usuario.setRole(requestDTO.getRole());

        // Atualizar senha apenas se fornecida
        if (requestDTO.getSenha() != null && !requestDTO.getSenha().trim().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(requestDTO.getSenha()));
        }

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        log.info("Usuário atualizado com sucesso - ID: {}", updatedUsuario.getId());

        return toResponseDTO(updatedUsuario);
    }

    // MÉTODO NOVO - Deletar usuário (soft delete)
    public void delete(Long id) {
        log.info("Realizando soft delete do usuário ID: {}", id);

        if (!usuarioRepository.existsActiveById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }

        usuarioRepository.softDelete(id);
        log.info("Soft delete realizado com sucesso para usuário ID: {}", id);
    }

    // MÉTODO EXISTENTE - Criar usuário
    public UsuarioResponseDTO create(UsuarioCreateRequestDTO requestDTO) {
        log.info("Criando novo usuário: {}", requestDTO.getEmail());

        // Verificar se email já existe
        if (usuarioRepository.existsActiveByEmail(requestDTO.getEmail())) {
            throw new BusinessException("Email já está em uso: " + requestDTO.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(requestDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(requestDTO.getSenha()));
        usuario.setNome(requestDTO.getNome());
        usuario.setTelefone(requestDTO.getTelefone());
        usuario.setRole(requestDTO.getRole());

        Usuario savedUsuario = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso - ID: {}", savedUsuario.getId());

        return toResponseDTO(savedUsuario);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNome(usuario.getNome());
        dto.setTelefone(usuario.getTelefone());
        dto.setRole(usuario.getRole());
        dto.setCreatedAt(usuario.getCreatedAt());
        dto.setUpdatedAt(usuario.getUpdatedAt());

        // Não incluir senha por segurança
        return dto;
    }
}
