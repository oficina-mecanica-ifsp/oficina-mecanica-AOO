package com.projetoweb.oficinamecanica.services;

import com.projetoweb.oficinamecanica.dto.LoginRequestDto;
import com.projetoweb.oficinamecanica.dto.LoginResponseDto;
import com.projetoweb.oficinamecanica.dto.UsuarioRequestDto;
import com.projetoweb.oficinamecanica.dto.UsuarioResponseDto;
import com.projetoweb.oficinamecanica.entities.Usuario;
import com.projetoweb.oficinamecanica.exceptions.RegraDeNegocioException;
import com.projetoweb.oficinamecanica.repositories.UsuarioRepository;
import com.projetoweb.oficinamecanica.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UsuarioResponseDto cadastrar(UsuarioRequestDto dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("E-mail já cadastrado: " + dto.email());
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setTipo(dto.tipo());
        return UsuarioResponseDto.from(usuarioRepository.save(usuario));
    }

    public LoginResponseDto autenticar(LoginRequestDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.senha())
        );
        Usuario usuario = usuarioRepository.findByEmail(dto.email()).orElseThrow();
        String token = jwtUtil.gerarToken(usuario);
        return new LoginResponseDto(token, "Bearer", usuario.getEmail(), usuario.getTipo());
    }

    public List<UsuarioResponseDto> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDto::from)
                .collect(java.util.stream.Collectors.toList());
    }
}
