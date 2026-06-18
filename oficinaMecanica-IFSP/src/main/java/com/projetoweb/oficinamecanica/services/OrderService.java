package com.projetoweb.oficinamecanica.services;

import com.projetoweb.oficinamecanica.dto.AtualizacaoStatusRequestDto;
import com.projetoweb.oficinamecanica.dto.OrderProdutoRequestDto;
import com.projetoweb.oficinamecanica.dto.OrderRequestDto;
import com.projetoweb.oficinamecanica.dto.OrderResponseDto;
import com.projetoweb.oficinamecanica.dto.OrderServicoRequestDto;
import com.projetoweb.oficinamecanica.entities.*;
import com.projetoweb.oficinamecanica.entities.enums.OrderStatus;
import com.projetoweb.oficinamecanica.entities.pk.OrderProdutoPK;
import com.projetoweb.oficinamecanica.entities.pk.OrderServicoPK;
import com.projetoweb.oficinamecanica.exceptions.RegraDeNegocioException;
import com.projetoweb.oficinamecanica.exceptions.ResourceNotFoundException;
import com.projetoweb.oficinamecanica.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClienteRepository clienteRepository;
    private final CarroRepository carroRepository;
    private final ProdutoRepository produtoRepository;
    private final ServicoRepository servicoRepository;
    private final OrderProdutoRepository orderProdutoRepository;
    private final OrderServicoRepository orderServicoRepository;
    private final PagamentoService pagamentoService;
    private final EstoqueService estoqueService;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository,
                        ClienteRepository clienteRepository,
                        CarroRepository carroRepository,
                        ProdutoRepository produtoRepository,
                        ServicoRepository servicoRepository,
                        OrderProdutoRepository orderProdutoRepository,
                        OrderServicoRepository orderServicoRepository,
                        PagamentoService pagamentoService,
                        EstoqueService estoqueService,
                        EmailService emailService) {
        this.orderRepository = orderRepository;
        this.clienteRepository = clienteRepository;
        this.carroRepository = carroRepository;
        this.produtoRepository = produtoRepository;
        this.servicoRepository = servicoRepository;
        this.orderProdutoRepository = orderProdutoRepository;
        this.orderServicoRepository = orderServicoRepository;
        this.pagamentoService = pagamentoService;
        this.estoqueService = estoqueService;
        this.emailService = emailService;
    }

    public OrderResponseDto findById(Long id) {
        Order order = orderRepository.findWithItensById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrada com id: " + id));
        return new OrderResponseDto(order);
    }

    public Page<OrderResponseDto> findAll(Pageable pageable) {
        return orderRepository.findAllWithItens(pageable).map(OrderResponseDto::new);
    }

    @Transactional
    public OrderResponseDto insert(OrderRequestDto dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + dto.clienteId()));

        Order order = new Order();
        order.setCliente(cliente);
        order.setOrderStatus(OrderStatus.ABERTO);

        if (dto.carroId() != null) {
            Carro carro = carroRepository.findById(dto.carroId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carro não encontrado com id: " + dto.carroId()));
            validarCarroPertenceAoCliente(carro, cliente);
            order.setCarro(carro);
        }

        return new OrderResponseDto(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDto update(Long id, OrderRequestDto dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrada com id: " + id));

        atualizarDadosCadastrais(order, dto);
        orderRepository.save(order);

        return new OrderResponseDto(orderRepository.findWithItensById(id).orElseThrow());
    }

    @Transactional
    public OrderResponseDto atualizarStatus(Long id, AtualizacaoStatusRequestDto dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrada com id: " + id));

        OrderStatus statusAnterior = order.getOrderStatus();

        if (!statusAnterior.canTransitionTo(dto.status())) {
            throw new RegraDeNegocioException(
                    "Transição inválida: " + statusAnterior + " → " + dto.status() +
                    ". Transições permitidas: " + statusAnterior.nextStates()
            );
        }

        if (dto.status() == OrderStatus.FINALIZADO) {
            if (!pagamentoService.existePagamentoParaOrder(id)) {
                throw new RegraDeNegocioException(
                        "A OS (id=" + id + ") só pode ser finalizada após o registro de pagamento."
                );
            }
        }

        order.setOrderStatus(dto.status());
        order.setDataValidade(dto.dataValidade());
        orderRepository.save(order);

        Order orderAtualizada = orderRepository.findWithItensById(id).orElseThrow();

        if (!statusAnterior.equals(dto.status())) {
            emailService.enviarAtualizacaoStatus(
                    orderAtualizada.getId(),
                    orderAtualizada.getCliente().getNome(),
                    orderAtualizada.getCliente().getEmail(),
                    dto.status(),
                    orderAtualizada.getTotal()
            );
        }

        return new OrderResponseDto(orderAtualizada);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order não encontrada com id: " + id);
        }
        orderRepository.deleteById(id);
    }

    // --- ITENS DA ORDER ---

    @Transactional
    public OrderResponseDto adicionarProduto(Long orderId, OrderProdutoRequestDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrada com id: " + orderId));

        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + dto.produtoId()));

        OrderProdutoPK pk = new OrderProdutoPK();
        pk.setOrderId(orderId);
        pk.setProdutoId(dto.produtoId());
        if (orderProdutoRepository.existsById(pk)) {
            throw new RegraDeNegocioException(
                    "Produto (id=" + dto.produtoId() + ") já está na order (id=" + orderId + ")."
            );
        }

        estoqueService.decrementar(produto, dto.quantidade());

        OrderProduto item = new OrderProduto(order, produto, produto.getNome(), produto.getPreco(), dto.quantidade());
        orderProdutoRepository.save(item);

        return new OrderResponseDto(orderRepository.findWithItensById(orderId).orElseThrow());
    }

    @Transactional
    public OrderResponseDto removerProduto(Long orderId, Long produtoId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order não encontrada com id: " + orderId);
        }

        OrderProdutoPK pk = new OrderProdutoPK();
        pk.setOrderId(orderId);
        pk.setProdutoId(produtoId);
        OrderProduto orderProduto = orderProdutoRepository.findById(pk)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto (id=" + produtoId + ") não encontrado na order (id=" + orderId + ")."
                ));

        estoqueService.incrementar(orderProduto.getProduto(), orderProduto.getQuantidade());
        orderProdutoRepository.delete(orderProduto);

        return new OrderResponseDto(orderRepository.findWithItensById(orderId).orElseThrow());
    }

    @Transactional
    public OrderResponseDto adicionarServico(Long orderId, OrderServicoRequestDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrada com id: " + orderId));

        Servico servico = servicoRepository.findById(dto.servicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com id: " + dto.servicoId()));

        OrderServicoPK pk = new OrderServicoPK();
        pk.setOrderId(orderId);
        pk.setServicoId(dto.servicoId());
        if (orderServicoRepository.existsById(pk)) {
            throw new RegraDeNegocioException(
                    "Serviço (id=" + dto.servicoId() + ") já está na order (id=" + orderId + ")."
            );
        }

        OrderServico item = new OrderServico(order, servico, servico.getNome(), servico.getPreco(),
                servico.getDescricao(), servico.getDuracao());
        orderServicoRepository.save(item);

        return new OrderResponseDto(orderRepository.findWithItensById(orderId).orElseThrow());
    }

    @Transactional
    public OrderResponseDto removerServico(Long orderId, Long servicoId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order não encontrada com id: " + orderId);
        }

        OrderServicoPK pk = new OrderServicoPK();
        pk.setOrderId(orderId);
        pk.setServicoId(servicoId);
        if (!orderServicoRepository.existsById(pk)) {
            throw new ResourceNotFoundException(
                    "Serviço (id=" + servicoId + ") não encontrado na order (id=" + orderId + ")."
            );
        }

        orderServicoRepository.deleteById(pk);
        return new OrderResponseDto(orderRepository.findWithItensById(orderId).orElseThrow());
    }

    // --- PRIVADOS ---

    private void atualizarDadosCadastrais(Order order, OrderRequestDto dto) {
        if (dto.clienteId() != null && !order.getCliente().getId().equals(dto.clienteId())) {
            Cliente novoCliente = clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + dto.clienteId()));

            if (order.getCarro() != null && dto.carroId() == null) {
                Carro carroAtual = order.getCarro();
                if (carroAtual.getCliente() == null || !carroAtual.getCliente().getId().equals(novoCliente.getId())) {
                    throw new RegraDeNegocioException(
                            "O carro atual (id=" + carroAtual.getId() + ") não pertence ao novo cliente (id=" + novoCliente.getId() + "). " +
                            "Informe um carroId válido ou omita o carro."
                    );
                }
            }
            order.setCliente(novoCliente);
        }

        if (dto.carroId() != null) {
            if (order.getCarro() == null || !order.getCarro().getId().equals(dto.carroId())) {
                Carro carro = carroRepository.findById(dto.carroId())
                        .orElseThrow(() -> new ResourceNotFoundException("Carro não encontrado com id: " + dto.carroId()));
                validarCarroPertenceAoCliente(carro, order.getCliente());
                order.setCarro(carro);
            }
        }
    }

    private void validarCarroPertenceAoCliente(Carro carro, Cliente cliente) {
        if (carro.getCliente() == null || !carro.getCliente().getId().equals(cliente.getId())) {
            throw new RegraDeNegocioException(
                    "O carro (id=" + carro.getId() + ") não pertence ao cliente (id=" + cliente.getId() + ")."
            );
        }
    }
}
