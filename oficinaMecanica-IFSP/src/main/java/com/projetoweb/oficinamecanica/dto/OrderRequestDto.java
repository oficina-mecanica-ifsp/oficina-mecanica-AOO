package com.projetoweb.oficinamecanica.dto;

import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
        @NotNull(message = "ID do cliente é obrigatório") Long clienteId,
        Long carroId
) {
}
