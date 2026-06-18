package com.projetoweb.oficinamecanica.controller;

import com.projetoweb.oficinamecanica.dto.LoginRequestDto;
import com.projetoweb.oficinamecanica.dto.LoginResponseDto;
import com.projetoweb.oficinamecanica.dto.UsuarioRequestDto;
import com.projetoweb.oficinamecanica.dto.UsuarioResponseDto;
import com.projetoweb.oficinamecanica.entities.enums.TipoUsuario;
import com.projetoweb.oficinamecanica.repositories.UsuarioRepository;
import com.projetoweb.oficinamecanica.services.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:it-testdb;DB_CLOSE_DELAY=-1")
@DisplayName("AuthController — Testes de Integração")
class AuthControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String adminToken;
    private final List<Long> createdUserIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        adminToken = loginAndGetToken("admin@oficina.com", "admin123");
    }

    @AfterEach
    void tearDown() {
        createdUserIds.forEach(id -> {
            if (usuarioRepository.existsById(id)) {
                usuarioRepository.deleteById(id);
            }
        });
        createdUserIds.clear();
    }

    // ---------------------------------------------------------------
    // POST /auth/cadastrar — controle de acesso
    // ---------------------------------------------------------------

    @Test
    @DisplayName("cadastrar — 401 sem token")
    void cadastrar_semToken_deveRetornar401() {
        HttpEntity<UsuarioRequestDto> request = new HttpEntity<>(
                new UsuarioRequestDto("Teste", "teste@it.com", "senha123", TipoUsuario.ATENDENTE));

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/cadastrar", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("cadastrar — 403 com role não-ADMIN")
    void cadastrar_comRoleAtendente_deveRetornar403() {
        Long atendenteId = criarUsuario("Atendente Temp", "atendente.temp@it.com", "senha123", TipoUsuario.ATENDENTE);
        String atendenteToken = loginAndGetToken("atendente.temp@it.com", "senha123");

        HttpEntity<UsuarioRequestDto> request = new HttpEntity<>(
                new UsuarioRequestDto("Novo", "novo@it.com", "senha123", TipoUsuario.MECANICO),
                authHeaders(atendenteToken));

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/cadastrar", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("cadastrar — 201 com ADMIN token e dados válidos")
    void cadastrar_adminComDadosValidos_deveRetornar201() {
        HttpEntity<UsuarioRequestDto> request = new HttpEntity<>(
                new UsuarioRequestDto("Mecanico Novo", "mecanico.novo@it.com", "senha123", TipoUsuario.MECANICO),
                authHeaders(adminToken));

        ResponseEntity<UsuarioResponseDto> response =
                restTemplate.postForEntity("/auth/cadastrar", request, UsuarioResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().email()).isEqualTo("mecanico.novo@it.com");
        assertThat(response.getBody().tipo()).isEqualTo(TipoUsuario.MECANICO);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        createdUserIds.add(response.getBody().id());
    }

    @Test
    @DisplayName("cadastrar — 400 com campo obrigatório ausente")
    void cadastrar_semNome_deveRetornar400() {
        HttpEntity<UsuarioRequestDto> request = new HttpEntity<>(
                new UsuarioRequestDto("", "invalido@it.com", "senha123", TipoUsuario.ATENDENTE),
                authHeaders(adminToken));

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/cadastrar", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ---------------------------------------------------------------
    // POST /auth/login
    // ---------------------------------------------------------------

    @Test
    @DisplayName("login — 200 com token quando credenciais corretas")
    void login_credenciaisCorretas_deveRetornar200ComToken() {
        Long id = criarUsuario("Mecanico Login", "mecanico.login@it.com", "senha123", TipoUsuario.MECANICO);

        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity("/auth/login",
                new LoginRequestDto("mecanico.login@it.com", "senha123"), LoginResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().token()).isNotBlank();
        assertThat(response.getBody().role()).isEqualTo(TipoUsuario.MECANICO);
    }

    @Test
    @DisplayName("login — 401 com senha errada")
    void login_senhaErrada_deveRetornar401() {
        Long id = criarUsuario("Atendente Login", "atendente.login@it.com", "senha123", TipoUsuario.ATENDENTE);

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/login",
                new LoginRequestDto("atendente.login@it.com", "senhaErrada"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("login — 401 com email inexistente")
    void login_emailInexistente_deveRetornar401() {
        ResponseEntity<String> response = restTemplate.postForEntity("/auth/login",
                new LoginRequestDto("naoexiste@it.com", "senha123"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private String loginAndGetToken(String email, String senha) {
        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(
                "/auth/login", new LoginRequestDto(email, senha), LoginResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody().token();
    }

    private Long criarUsuario(String nome, String email, String senha, TipoUsuario tipo) {
        HttpEntity<UsuarioRequestDto> request = new HttpEntity<>(
                new UsuarioRequestDto(nome, email, senha, tipo), authHeaders(adminToken));
        ResponseEntity<UsuarioResponseDto> response =
                restTemplate.postForEntity("/auth/cadastrar", request, UsuarioResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long id = response.getBody().id();
        createdUserIds.add(id);
        return id;
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
