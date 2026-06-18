package com.projetoweb.oficinamecanica.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.projetoweb.oficinamecanica.entities.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AtualizacaoStatusRequestDto(
        @NotNull(message = "Status é obrigatório") OrderStatus status,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dataValidade
) {
}
