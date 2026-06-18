package com.projetoweb.oficinamecanica.config;

import com.projetoweb.oficinamecanica.entities.*;
import com.projetoweb.oficinamecanica.entities.enums.OrderStatus;
import com.projetoweb.oficinamecanica.entities.enums.TipoProduto;
import com.projetoweb.oficinamecanica.entities.enums.TipoUsuario;
import com.projetoweb.oficinamecanica.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final CarroRepository carroRepository;
    private final OrderRepository orderRepository;
    private final ProdutoRepository produtoRepository;
    private final ServicoRepository servicoRepository;
    private final OrderServicoRepository orderServicoRepository;
    private final OrderProdutoRepository orderProdutoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public TestConfig(ClienteRepository clienteRepository,
                      CarroRepository carroRepository,
                      OrderRepository orderRepository,
                      ProdutoRepository produtoRepository,
                      ServicoRepository servicoRepository,
                      OrderServicoRepository orderServicoRepository,
                      OrderProdutoRepository orderProdutoRepository,
                      UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.carroRepository = carroRepository;
        this.orderRepository = orderRepository;
        this.produtoRepository = produtoRepository;
        this.servicoRepository = servicoRepository;
        this.orderServicoRepository = orderServicoRepository;
        this.orderProdutoRepository = orderProdutoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        Usuario admin = new Usuario();
        admin.setNome("Admin Teste");
        admin.setEmail("admin@oficina.com");
        admin.setSenha(passwordEncoder.encode("admin123"));
        admin.setTipo(TipoUsuario.ADMIN);
        usuarioRepository.save(admin);

        // Documentos únicos e válidos: 11 dígitos (CPF)
        Cliente c1 = new Cliente(null, "Matheus", "11922233378", "matheus@gmail.com", "12345678901");
        Cliente c2 = new Cliente(null, "Maria", "11933344487", "maria@gmail.com", "98765432100");

        clienteRepository.saveAll(Arrays.asList(c1, c2));

        Carro carro1 = new Carro(null, "Fiat Uno", "ABC-1234", "Azul", 2022, "Fiat", c1);
        Carro carro2 = new Carro(null, "Chevrolet Onix", "DEF-5678", "Vermelho", 2021, "Chevrolet", c2);

        carroRepository.saveAll(Arrays.asList(carro1, carro2));

        Order o1 = new Order(null, OrderStatus.ABERTO, c1);
        Order o2 = new Order(null, OrderStatus.ABERTO, c2);

        orderRepository.saveAll(Arrays.asList(o1, o2));

        Servico s1 = new Servico(null, "Trocar Pneu", new BigDecimal("100.00"), "Realizar a troca do pneu", Duration.ofHours(2));
        Servico s2 = new Servico(null, "Trocar de Oleo", new BigDecimal("100.00"), "Realizar a troca do oleo", Duration.ofHours(1));

        servicoRepository.saveAll(Arrays.asList(s1, s2));

        OrderServico os1 = new OrderServico(o1, s1, s1.getNome(), s1.getPreco(), s1.getDescricao(), s1.getDuracao());
        OrderServico os2 = new OrderServico(o2, s2, s2.getNome(), s2.getPreco(), s2.getDescricao(), s2.getDuracao());

        orderServicoRepository.saveAll(Arrays.asList(os1, os2));

        Produto p1 = new Produto(null, "Pneu", new BigDecimal("50.00"), 4, TipoProduto.PECA, 2);
        Produto p2 = new Produto(null, "Oleo", new BigDecimal("100.00"), 1, TipoProduto.PRODUTO, 0);

        produtoRepository.saveAll(Arrays.asList(p1, p2));

        OrderProduto op1 = new OrderProduto(o1, p1, p1.getNome(), p1.getPreco(), p1.getQuantidade());
        OrderProduto op2 = new OrderProduto(o2, p2, p2.getNome(), p2.getPreco(), p2.getQuantidade());

        orderProdutoRepository.saveAll(Arrays.asList(op1, op2));
    }
}
