package com.projetoweb.oficinamecanica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequestDto(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres") String nome,
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos") String telefone,
        @Email(message = "Email inválido")
        @NotBlank(message = "Email é obrigatório") String email,
        @NotBlank(message = "Documento é obrigatório")
        @Pattern(regexp = "\\d{11}|\\d{14}",
                message = "Documento deve ter 11 dígitos (CPF) ou 14 dígitos (CNPJ), apenas números") String doc
) {
}
