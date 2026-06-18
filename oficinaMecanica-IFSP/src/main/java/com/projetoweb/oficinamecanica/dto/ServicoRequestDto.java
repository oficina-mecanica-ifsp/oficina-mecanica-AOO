package com.projetoweb.oficinamecanica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Duration;

public record ServicoRequestDto(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @Positive(message = "Preço deve ser positivo") BigDecimal preco,
        String descricao,
        Duration duracao
) {
}
