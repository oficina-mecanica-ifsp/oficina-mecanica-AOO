package com.projetoweb.oficinamecanica.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderProdutoRequestDto(
        @NotNull(message = "Produto é obrigatório") Long produtoId,
        @Positive(message = "Quantidade deve ser positiva") Integer quantidade
) {
}
