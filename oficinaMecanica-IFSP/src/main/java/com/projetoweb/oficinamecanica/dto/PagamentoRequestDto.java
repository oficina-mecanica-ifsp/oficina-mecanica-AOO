package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.enums.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PagamentoRequestDto(
        @NotNull(message = "ID da OS é obrigatório") Long orderId,
        @NotNull(message = "Valor é obrigatório") @Positive(message = "Valor deve ser positivo") BigDecimal valor,
        @NotNull(message = "Forma de pagamento é obrigatória") FormaPagamento formaPagamento
) {
}
