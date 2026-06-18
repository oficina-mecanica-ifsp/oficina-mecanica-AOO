package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.Usuario;
import com.projetoweb.oficinamecanica.entities.enums.TipoUsuario;

public record UsuarioResponseDto(
        Long id,
        String nome,
        String email,
        TipoUsuario tipo
) {
    public static UsuarioResponseDto from(Usuario entity) {
        return new UsuarioResponseDto(entity.getId(), entity.getNome(), entity.getEmail(), entity.getTipo());
    }
}
