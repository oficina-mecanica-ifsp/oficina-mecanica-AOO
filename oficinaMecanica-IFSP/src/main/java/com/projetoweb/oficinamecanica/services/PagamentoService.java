package com.projetoweb.oficinamecanica.services;

import com.projetoweb.oficinamecanica.dto.PagamentoRequestDto;
import com.projetoweb.oficinamecanica.dto.PagamentoResponseDto;
import com.projetoweb.oficinamecanica.entities.Order;
import com.projetoweb.oficinamecanica.entities.Pagamento;
import com.projetoweb.oficinamecanica.exceptions.RegraDeNegocioException;
import com.projetoweb.oficinamecanica.exceptions.ResourceNotFoundException;
import com.projetoweb.oficinamecanica.repositories.OrderRepository;
import com.projetoweb.oficinamecanica.repositories.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final OrderRepository orderRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository, OrderRepository orderRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.orderRepository = orderRepository;
    }

    public boolean existePagamentoParaOrder(Long orderId) {
        return pagamentoRepository.existsByOrderId(orderId);
    }

    public PagamentoResponseDto findByOrderId(Long orderId) {
        Pagamento pagamento = pagamentoRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado para OS id=" + orderId));
        return PagamentoResponseDto.from(pagamento);
    }

    @Transactional
    public PagamentoResponseDto registrar(PagamentoRequestDto dto) {
        Order order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("OS não encontrada com id: " + dto.orderId()));

        if (pagamentoRepository.existsByOrderId(dto.orderId())) {
            throw new RegraDeNegocioException(
                    "Já existe pagamento registrado para a OS id=" + dto.orderId()
            );
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setOrder(order);
        pagamento.setValor(dto.valor());
        pagamento.setFormaPagamento(dto.formaPagamento());
        pagamento.setDataPagamento(Instant.now());

        return PagamentoResponseDto.from(pagamentoRepository.save(pagamento));
    }
}
