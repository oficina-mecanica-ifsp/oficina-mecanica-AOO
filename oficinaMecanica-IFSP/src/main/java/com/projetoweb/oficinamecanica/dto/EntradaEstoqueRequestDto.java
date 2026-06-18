package com.projetoweb.oficinamecanica.dto;

import jakarta.validation.constraints.Positive;

public record EntradaEstoqueRequestDto(
        @Positive(message = "Quantidade deve ser positiva") Integer quantidade
) {
}
