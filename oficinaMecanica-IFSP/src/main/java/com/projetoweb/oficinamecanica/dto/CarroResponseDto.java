package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.Carro;

public record CarroResponseDto(Long id, String modelo, String placa, String cor, Integer anoFabricacao, String marca,
                               Long clienteId, String clienteNome, String clienteDoc) {

    public static CarroResponseDto from(Carro carro) {
        return new CarroResponseDto(
                carro.getId(),
                carro.getModelo(),
                carro.getPlaca(),
                carro.getCor(),
                carro.getAnoFabricacao(),
                carro.getMarca(),
                carro.getCliente().getId(),
                carro.getCliente().getNome(),
                carro.getCliente().getDoc()
        );
    }
}