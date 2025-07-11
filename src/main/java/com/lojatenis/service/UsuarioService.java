package com.lojatenis.service;

import com.lojatenis.domain.Usuario;
import com.lojatenis.dto.request.UsuarioCreateRequestDTO;
import com.lojatenis.dto.response.UsuarioResponseDTO;
import com.lojatenis.exception.BusinessException;
import com.lojatenis.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
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
        return dto;
    }
}
