package com.projetoweb.oficinamecanica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CarroRequestDto(
        @NotBlank(message = "Modelo é obrigatório") String modelo,
        @NotBlank(message = "Placa é obrigatória")
        @Pattern(regexp = "[A-Z]{3}-\\d{4}", message = "Placa deve estar no formato ABC-1234") String placa,
        String cor,
        @NotNull(message = "Ano de fabricação é obrigatório") Integer anoFabricacao,
        @NotBlank(message = "Marca é obrigatória") String marca,
        @NotNull(message = "Cliente é obrigatório") Long clienteId
) {
}
