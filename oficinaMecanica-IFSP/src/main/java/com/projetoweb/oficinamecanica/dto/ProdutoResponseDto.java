package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.Produto;
import com.projetoweb.oficinamecanica.entities.enums.TipoProduto;

import java.math.BigDecimal;

public record ProdutoResponseDto(
        Long id,
        String nome,
        BigDecimal preco,
        Integer quantidade,
        TipoProduto tipo,
        Integer quantidadeMinima
) {

    public static ProdutoResponseDto from(Produto produto) {
        return new ProdutoResponseDto(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getQuantidade(),
                produto.getTipo(),
                produto.getQuantidadeMinima()
        );
    }
}
