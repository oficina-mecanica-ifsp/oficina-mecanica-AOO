package com.projetoweb.oficinamecanica.controller;

import com.projetoweb.oficinamecanica.dto.AtualizacaoStatusRequestDto;
import com.projetoweb.oficinamecanica.dto.LoginRequestDto;
import com.projetoweb.oficinamecanica.dto.LoginResponseDto;
import com.projetoweb.oficinamecanica.dto.OrderRequestDto;
import com.projetoweb.oficinamecanica.dto.OrderResponseDto;
import com.projetoweb.oficinamecanica.dto.UsuarioRequestDto;
import com.projetoweb.oficinamecanica.dto.UsuarioResponseDto;
import com.projetoweb.oficinamecanica.entities.enums.OrderStatus;
import com.projetoweb.oficinamecanica.entities.enums.TipoUsuario;
import com.projetoweb.oficinamecanica.repositories.ClienteRepository;
import com.projetoweb.oficinamecanica.repositories.OrderRepository;
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
@DisplayName("OrderController — Testes de Integração (Segurança + Fluxo)")
class OrderControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private OrderRepository orderRepository;

    private String adminToken;
    private String atendenteToken;
    private String mecanicoToken;
    private Long clienteId;

    private final List<Long> createdOrderIds = new ArrayList<>();
    private final List<Long> createdUserIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        adminToken = loginAndGetToken("admin@oficina.com", "admin123");

        Long atendenteId = criarUsuario("Atendente IT", "atendente@order.com", "senha123", TipoUsuario.ATENDENTE);
        Long mecanicoId = criarUsuario("Mecanico IT", "mecanico@order.com", "senha123", TipoUsuario.MECANICO);
        createdUserIds.add(atendenteId);
        createdUserIds.add(mecanicoId);

        atendenteToken = loginAndGetToken("atendente@order.com", "senha123");
        mecanicoToken = loginAndGetToken("mecanico@order.com", "senha123");

        clienteId = clienteRepository.findAll().get(0).getId();
    }

    @AfterEach
    void tearDown() {
        createdOrderIds.forEach(id -> {
            if (orderRepository.existsById(id)) orderRepository.deleteById(id);
        });
        createdOrderIds.clear();

        createdUserIds.forEach(id -> {
            if (usuarioRepository.existsById(id)) usuarioRepository.deleteById(id);
        });
        createdUserIds.clear();
    }

    // ---------------------------------------------------------------
    // 401 — sem token
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /orders — 401 sem token")
    void getOrders_semToken_deveRetornar401() {
        ResponseEntity<String> response = restTemplate.getForEntity("/orders", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("POST /orders — 401 sem token")
    void createOrder_semToken_deveRetornar401() {
        HttpEntity<OrderRequestDto> request = new HttpEntity<>(new OrderRequestDto(clienteId, null));
        ResponseEntity<String> response = restTemplate.postForEntity("/orders", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ---------------------------------------------------------------
    // 403 — role incorreto
    // ---------------------------------------------------------------

    @Test
    @DisplayName("POST /orders com MECANICO — 403")
    void createOrder_comRoleMecanico_deveRetornar403() {
        HttpEntity<OrderRequestDto> request = new HttpEntity<>(
                new OrderRequestDto(clienteId, null), authHeaders(mecanicoToken));

        ResponseEntity<String> response = restTemplate.postForEntity("/orders", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("PATCH /orders/{id}/status com ATENDENTE — 403")
    void atualizarStatus_comRoleAtendente_deveRetornar403() {
        Long orderId = criarOrder(atendenteToken);

        AtualizacaoStatusRequestDto dto = new AtualizacaoStatusRequestDto(OrderStatus.EM_ANDAMENTO, null);
        HttpEntity<AtualizacaoStatusRequestDto> request = new HttpEntity<>(dto, authHeaders(atendenteToken));

        ResponseEntity<String> response = restTemplate.exchange(
                "/orders/" + orderId + "/status", HttpMethod.PATCH, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ---------------------------------------------------------------
    // 2xx — fluxo feliz
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /orders com token — 200 com página")
    void getOrders_comToken_deveRetornar200() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/orders", HttpMethod.GET, new HttpEntity<>(authHeaders(atendenteToken)), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("content");
    }

    @Test
    @DisplayName("POST /orders com ATENDENTE — 201 e status RECEBIDO")
    void createOrder_comRoleAtendente_deveRetornar201() {
        HttpEntity<OrderRequestDto> request = new HttpEntity<>(
                new OrderRequestDto(clienteId, null), authHeaders(atendenteToken));

        ResponseEntity<OrderResponseDto> response =
                restTemplate.postForEntity("/orders", request, OrderResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatus()).isEqualTo(OrderStatus.ABERTO);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        createdOrderIds.add(response.getBody().getId());
    }

    @Test
    @DisplayName("PATCH /orders/{id}/status com MECANICO — transição válida → 200")
    void atualizarStatus_transicaoValida_deveRetornar200() {
        Long orderId = criarOrder(atendenteToken);

        AtualizacaoStatusRequestDto dto = new AtualizacaoStatusRequestDto(OrderStatus.EM_ANDAMENTO, null);
        HttpEntity<AtualizacaoStatusRequestDto> request = new HttpEntity<>(dto, authHeaders(mecanicoToken));

        ResponseEntity<OrderResponseDto> response = restTemplate.exchange(
                "/orders/" + orderId + "/status", HttpMethod.PATCH, request, OrderResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(OrderStatus.EM_ANDAMENTO);
    }

    // ---------------------------------------------------------------
    // 422 — regra de negócio
    // ---------------------------------------------------------------

    @Test
    @DisplayName("PATCH /orders/{id}/status com MECANICO — transição inválida → 422")
    void atualizarStatus_transicaoInvalida_deveRetornar422() {
        Long orderId = criarOrder(atendenteToken);

        AtualizacaoStatusRequestDto dto = new AtualizacaoStatusRequestDto(OrderStatus.FINALIZADO, null);
        HttpEntity<AtualizacaoStatusRequestDto> request = new HttpEntity<>(dto, authHeaders(mecanicoToken));

        ResponseEntity<String> response = restTemplate.exchange(
                "/orders/" + orderId + "/status", HttpMethod.PATCH, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
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
        return response.getBody().id();
    }

    private Long criarOrder(String token) {
        HttpEntity<OrderRequestDto> request = new HttpEntity<>(
                new OrderRequestDto(clienteId, null), authHeaders(token));
        ResponseEntity<OrderResponseDto> response =
                restTemplate.postForEntity("/orders", request, OrderResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long id = response.getBody().getId();
        createdOrderIds.add(id);
        return id;
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
