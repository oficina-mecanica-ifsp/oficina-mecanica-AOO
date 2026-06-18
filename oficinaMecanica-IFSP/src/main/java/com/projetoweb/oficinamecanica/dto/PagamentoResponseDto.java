package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.Pagamento;
import com.projetoweb.oficinamecanica.entities.enums.FormaPagamento;

import java.math.BigDecimal;
import java.time.Instant;

public record PagamentoResponseDto(
        Long id,
        Long orderId,
        BigDecimal valor,
        FormaPagamento formaPagamento,
        Instant dataPagamento
) {
    public static PagamentoResponseDto from(Pagamento entity) {
        return new PagamentoResponseDto(
                entity.getId(),
                entity.getOrder().getId(),
                entity.getValor(),
                entity.getFormaPagamento(),
                entity.getDataPagamento()
        );
    }
}
