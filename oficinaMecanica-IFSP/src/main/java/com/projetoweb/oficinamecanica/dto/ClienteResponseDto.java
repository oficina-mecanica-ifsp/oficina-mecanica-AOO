package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.Cliente;

public record ClienteResponseDto(Long id, String nome, String telefone, String email, String doc) {

    public static ClienteResponseDto from(Cliente cliente) {
        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getDoc()
        );
    }
}
