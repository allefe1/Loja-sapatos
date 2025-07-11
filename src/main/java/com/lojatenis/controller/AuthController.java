package com.lojatenis.controller;

import com.lojatenis.dto.request.LoginRequestDTO;
import com.lojatenis.dto.request.UsuarioCreateRequestDTO;
import com.lojatenis.dto.response.JwtResponseDTO;
import com.lojatenis.dto.response.UsuarioResponseDTO;
import com.lojatenis.security.JwtTokenUtil;
import com.lojatenis.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil,
                          UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Tentativa de login para email: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getSenha()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);

        JwtResponseDTO response = new JwtResponseDTO(
                token,
                "Bearer",
                jwtTokenUtil.getExpirationDateFromToken(token)
        );

        log.info("Login realizado com sucesso para: {}", loginRequest.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(@Valid @RequestBody UsuarioCreateRequestDTO requestDTO) {
        log.info("Registro de novo usuário: {}", requestDTO.getEmail());

        UsuarioResponseDTO createdUser = usuarioService.create(requestDTO);

        log.info("Usuário registrado com sucesso: {}", createdUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
