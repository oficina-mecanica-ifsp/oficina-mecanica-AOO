package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.enums.TipoProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoRequestDto(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @Positive(message = "Preço deve ser positivo") BigDecimal preco,
        @PositiveOrZero(message = "Quantidade não pode ser negativa") Integer quantidade,
        @NotNull(message = "Tipo é obrigatório") TipoProduto tipo,
        @PositiveOrZero(message = "Quantidade mínima não pode ser negativa") Integer quantidadeMinima
) {
}
