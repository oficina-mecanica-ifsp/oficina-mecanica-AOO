package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.enums.TipoUsuario;

public record LoginResponseDto(
        String token,
        String tipo,
        String email,
        TipoUsuario role
) {
}
