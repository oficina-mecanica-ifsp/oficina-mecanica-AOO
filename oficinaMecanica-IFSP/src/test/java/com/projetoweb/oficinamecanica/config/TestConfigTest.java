package com.projetoweb.oficinamecanica.config;

import com.projetoweb.oficinamecanica.entities.*;
import com.projetoweb.oficinamecanica.entities.enums.OrderStatus;
import com.projetoweb.oficinamecanica.entities.OrderItem;
import com.projetoweb.oficinamecanica.repositories.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Testes de Carga de Dados - TestConfig")
class TestConfigTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private OrderServicoRepository orderServicoRepository;

    @Autowired
    private OrderProdutoRepository orderProdutoRepository;

    @Test
    @DisplayName("Deve carregar 2 clientes no banco")
    void deveCarregarDoisClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        
        assertEquals(2, clientes.size(), "Deve ter 2 clientes");
        
        Cliente c1 = clientes.get(0);
        assertEquals("Matheus", c1.getNome());
        assertEquals("11922233378", c1.getTelefone());
        assertEquals("matheus@gmail.com", c1.getEmail());
        assertEquals("12345678901", c1.getDoc());
        
        Cliente c2 = clientes.get(1);
        assertEquals("Maria", c2.getNome());
        assertEquals("11933344487", c2.getTelefone());
        assertEquals("maria@gmail.com", c2.getEmail());
    }

    @Test
    @DisplayName("Deve carregar 2 carros no banco")
    void deveCarregarDoisCarros() {
        List<Carro> carros = carroRepository.findAll();
        
        assertEquals(2, carros.size(), "Deve ter 2 carros");
        
        Carro carro1 = carros.get(0);
        assertEquals("Fiat Uno", carro1.getModelo());
        assertEquals("ABC-1234", carro1.getPlaca());
        assertEquals("Azul", carro1.getCor());
        assertEquals(2022, carro1.getAnoFabricacao());
        assertEquals("Fiat", carro1.getMarca());
        assertNotNull(carro1.getCliente(), "Carro deve ter um cliente");
        
        Carro carro2 = carros.get(1);
        assertEquals("Chevrolet Onix", carro2.getModelo());
        assertEquals("DEF-5678", carro2.getPlaca());
        assertEquals("Vermelho", carro2.getCor());
        assertEquals(2021, carro2.getAnoFabricacao());
        assertEquals("Chevrolet", carro2.getMarca());
    }

    @Test
    @DisplayName("Deve carregar 2 orders no banco")
    void deveCarregarDoisOrders() {
        List<Order> orders = orderRepository.findAll();
        
        assertEquals(2, orders.size(), "Deve ter 2 orders");
        
        Order o1 = orders.get(0);
        assertEquals(OrderStatus.ABERTO, o1.getOrderStatus());
        assertNotNull(o1.getCliente(), "Order deve ter um cliente");
        
        Order o2 = orders.get(1);
        assertEquals(OrderStatus.ABERTO, o2.getOrderStatus());
        assertNotNull(o2.getCliente(), "Order deve ter um cliente");
    }

    @Test
    @DisplayName("Deve carregar 2 serviços no banco")
    void deveCarregarDoisServicos() {
        List<Servico> servicos = servicoRepository.findAll();
        
        assertEquals(2, servicos.size(), "Deve ter 2 serviços");
        
        Servico s1 = servicos.get(0);
        assertEquals("Trocar Pneu", s1.getNome());
        assertEquals(0, new BigDecimal("100.00").compareTo(s1.getPreco()));
        assertEquals("Realizar a troca do pneu", s1.getDescricao());
        assertNotNull(s1.getDuracao(), "Serviço deve ter duração");

        Servico s2 = servicos.get(1);
        assertEquals("Trocar de Oleo", s2.getNome());
        assertEquals(0, new BigDecimal("100.00").compareTo(s2.getPreco()));
        assertEquals("Realizar a troca do oleo", s2.getDescricao());
    }

    @Test
    @DisplayName("Deve carregar 2 produtos no banco")
    void deveCarregarDoisProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        
        assertEquals(2, produtos.size(), "Deve ter 2 produtos");
        
        Produto p1 = produtos.get(0);
        assertEquals("Pneu", p1.getNome());
        assertEquals(0, new BigDecimal("50.00").compareTo(p1.getPreco()));
        assertEquals(4, p1.getQuantidade());

        Produto p2 = produtos.get(1);
        assertEquals("Oleo", p2.getNome());
        assertEquals(0, new BigDecimal("100.00").compareTo(p2.getPreco()));
        assertEquals(1, p2.getQuantidade());
    }

    @Test
    @DisplayName("Deve carregar 2 OrderServico no banco")
    void deveCarregarDoisOrderServico() {
        List<OrderServico> orderServicos = orderServicoRepository.findAll();
        
        assertEquals(2, orderServicos.size(), "Deve ter 2 OrderServico");
        
        OrderServico os1 = orderServicos.get(0);
        assertEquals("Trocar Pneu", os1.getNome());
        assertEquals(0, new BigDecimal("100.00").compareTo(os1.getPreco()));
        assertNotNull(os1.getOrder(), "OrderServico deve ter um Order");
        assertNotNull(os1.getServico(), "OrderServico deve ter um Servico");

        OrderServico os2 = orderServicos.get(1);
        assertEquals("Trocar de Oleo", os2.getNome());
        assertEquals(0, new BigDecimal("100.00").compareTo(os2.getPreco()));
    }

    @Test
    @DisplayName("Deve carregar 2 OrderProduto no banco")
    void deveCarregarDoisOrderProduto() {
        List<OrderProduto> orderProdutos = orderProdutoRepository.findAll();
        
        assertEquals(2, orderProdutos.size(), "Deve ter 2 OrderProduto");
        
        OrderProduto op1 = orderProdutos.get(0);
        assertEquals("Pneu", op1.getNome());
        assertEquals(0, new BigDecimal("50.00").compareTo(op1.getPreco()));
        assertEquals(4, op1.getQuantidade());
        assertNotNull(op1.getOrder(), "OrderProduto deve ter um Order");
        assertNotNull(op1.getProduto(), "OrderProduto deve ter um Produto");

        OrderProduto op2 = orderProdutos.get(1);
        assertEquals("Oleo", op2.getNome());
        assertEquals(0, new BigDecimal("100.00").compareTo(op2.getPreco()));
        assertEquals(1, op2.getQuantidade());
    }

    @Test
    @DisplayName("Deve calcular subtotal corretamente em OrderProduto")
    void deveCalcularSubtotalCorretamente() {
        List<OrderProduto> orderProdutos = orderProdutoRepository.findAll();
        
        OrderProduto op1 = orderProdutos.get(0);
        assertEquals(0, new BigDecimal("200.00").compareTo(op1.getSubTotal()),
                "Subtotal deve ser 50.00 * 4 = 200.00");

        OrderProduto op2 = orderProdutos.get(1);
        assertEquals(0, new BigDecimal("100.00").compareTo(op2.getSubTotal()),
                "Subtotal deve ser 100.00 * 1 = 100.00");
    }

    @Test
    @Transactional
    @DisplayName("Deve calcular total do Order corretamente")
    void deveCalcularTotalDoOrderCorretamente() {
        List<Order> orders = orderRepository.findAll();
        
        Order o1 = orders.get(0);
        // Total = OrderServico (100.00) + OrderProduto (50.00 * 4 = 200.00) = 300.00
        assertEquals(0, new BigDecimal("300.00").compareTo(o1.getTotal()),
                "Total do Order 1 deve ser 300.00");

        Order o2 = orders.get(1);
        // Total = OrderServico (100.00) + OrderProduto (100.00 * 1 = 100.00) = 200.00
        assertEquals(0, new BigDecimal("200.00").compareTo(o2.getTotal()),
                "Total do Order 2 deve ser 200.00");
    }

    @Test
    @DisplayName("Deve ter relacionamento correto entre Cliente e Carro")
    void deveTerRelacionamentoCorretoClienteCarro() {
        List<Cliente> clientes = clienteRepository.findAll();
        List<Carro> carros = carroRepository.findAll();
        
        Cliente c1 = clientes.get(0);
        Carro carro1 = carros.get(0);
        
        assertEquals(c1.getId(), carro1.getCliente().getId(), 
            "Carro 1 deve pertencer ao Cliente 1");
        
        Cliente c2 = clientes.get(1);
        Carro carro2 = carros.get(1);
        
        assertEquals(c2.getId(), carro2.getCliente().getId(), 
            "Carro 2 deve pertencer ao Cliente 2");
    }

    @Test
    @DisplayName("Deve ter relacionamento correto entre Order e Cliente")
    void deveTerRelacionamentoCorretoOrderCliente() {
        List<Order> orders = orderRepository.findAll();
        List<Cliente> clientes = clienteRepository.findAll();
        
        Order o1 = orders.get(0);
        Cliente c1 = clientes.get(0);
        
        assertEquals(c1.getId(), o1.getCliente().getId(), 
            "Order 1 deve pertencer ao Cliente 1");
        
        Order o2 = orders.get(1);
        Cliente c2 = clientes.get(1);
        
        assertEquals(c2.getId(), o2.getCliente().getId(), 
            "Order 2 deve pertencer ao Cliente 2");
    }

    @Test
    @DisplayName("Deve validar dados dos clientes")
    void deveValidarDadosClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        
        for (Cliente cliente : clientes) {
            assertNotNull(cliente.getId(), "Cliente deve ter ID");
            assertNotNull(cliente.getNome(), "Cliente deve ter nome");
            assertTrue(cliente.getNome().length() >= 3, "Nome deve ter pelo menos 3 caracteres");
            assertNotNull(cliente.getEmail(), "Cliente deve ter email");
            assertTrue(cliente.getEmail().contains("@"), "Email deve ser válido");
            assertNotNull(cliente.getTelefone(), "Cliente deve ter telefone");
            assertTrue(cliente.getTelefone().length() >= 10, "Telefone deve ter pelo menos 10 dígitos");
            assertNotNull(cliente.getDoc(), "Cliente deve ter documento");
        }
    }

    @Test
    @DisplayName("Deve validar dados dos carros")
    void deveValidarDadosCarros() {
        List<Carro> carros = carroRepository.findAll();
        
        for (Carro carro : carros) {
            assertNotNull(carro.getId(), "Carro deve ter ID");
            assertNotNull(carro.getModelo(), "Carro deve ter modelo");
            assertNotNull(carro.getPlaca(), "Carro deve ter placa");
            assertTrue(carro.getPlaca().matches("[A-Z]{3}-\\d{4}"), 
                "Placa deve estar no formato ABC-1234");
            assertNotNull(carro.getMarca(), "Carro deve ter marca");
            assertNotNull(carro.getAnoFabricacao(), "Carro deve ter ano de fabricação");
            assertTrue(carro.getAnoFabricacao() > 1900, "Ano deve ser válido");
        }
    }

    @Test
    @DisplayName("Deve validar dados dos produtos")
    void deveValidarDadosProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        
        for (Produto produto : produtos) {
            assertNotNull(produto.getId(), "Produto deve ter ID");
            assertNotNull(produto.getNome(), "Produto deve ter nome");
            assertNotNull(produto.getPreco(), "Produto deve ter preço");
            assertTrue(produto.getPreco().compareTo(BigDecimal.ZERO) > 0, "Preço deve ser positivo");
            assertNotNull(produto.getQuantidade(), "Produto deve ter quantidade");
            assertTrue(produto.getQuantidade() >= 0, "Quantidade não pode ser negativa");
        }
    }

    @Test
    @DisplayName("Deve validar dados dos serviços")
    void deveValidarDadosServicos() {
        List<Servico> servicos = servicoRepository.findAll();
        
        for (Servico servico : servicos) {
            assertNotNull(servico.getId(), "Serviço deve ter ID");
            assertNotNull(servico.getNome(), "Serviço deve ter nome");
            assertNotNull(servico.getPreco(), "Serviço deve ter preço");
            assertTrue(servico.getPreco().compareTo(BigDecimal.ZERO) > 0, "Preço deve ser positivo");
            assertNotNull(servico.getDescricao(), "Serviço deve ter descrição");
            assertNotNull(servico.getDuracao(), "Serviço deve ter duração");
        }
    }

    @Test
    @Transactional
    @DisplayName("Deve ter itens no Order")
    void deveTerItensNoOrder() {
        List<Order> orders = orderRepository.findAll();
        
        for (Order order : orders) {
            List<OrderItem> itens = order.getItens();
            assertNotNull(itens, "Order deve ter lista de itens");
            assertEquals(2, itens.size(), "Order deve ter 2 itens (1 serviço + 1 produto)");
            itens.forEach(item -> assertNotNull(item.getNome(), "Todo item deve ter nome"));
        }
    }
}
