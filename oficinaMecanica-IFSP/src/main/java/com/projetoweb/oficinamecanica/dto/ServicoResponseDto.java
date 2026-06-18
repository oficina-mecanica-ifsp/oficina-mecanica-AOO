package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.Servico;

import java.math.BigDecimal;
import java.time.Duration;

public record ServicoResponseDto(
        Long id,
        String nome,
        BigDecimal preco,
        String descricao,
        Duration duracao
) {

    public static ServicoResponseDto from(Servico servico) {
        return new ServicoResponseDto(
                servico.getId(),
                servico.getNome(),
                servico.getPreco(),
                servico.getDescricao(),
                servico.getDuracao()
        );
    }
}
