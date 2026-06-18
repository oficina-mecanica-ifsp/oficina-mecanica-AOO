package com.projetoweb.oficinamecanica.dto;

import jakarta.validation.constraints.NotNull;

public record OrderServicoRequestDto(
        @NotNull(message = "Serviço é obrigatório") Long servicoId
) {
}
