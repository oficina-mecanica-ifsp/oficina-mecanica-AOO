package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDto(
        @NotBlank(message = "Nome é obrigatório") @Size(min = 3, max = 100) String nome,
        @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email,
        @NotBlank(message = "Senha é obrigatória") @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres") String senha,
        @NotNull(message = "Tipo de usuário é obrigatório") TipoUsuario tipo
) {
}
