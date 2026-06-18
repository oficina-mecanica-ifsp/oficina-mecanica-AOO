package com.projetoweb.oficinamecanica.dto;

import com.projetoweb.oficinamecanica.entities.OrderItem;
import com.projetoweb.oficinamecanica.entities.OrderProduto;

import java.math.BigDecimal;

public record OrderItemDto(
        String tipo,
        String nome,
        BigDecimal preco,
        Integer quantidade,
        BigDecimal subTotal
) {
    public static OrderItemDto from(OrderItem item) {
        Integer quantidade = item instanceof OrderProduto op ? op.getQuantidade() : 1;
        return new OrderItemDto(
                item.getTipo(),
                item.getNome(),
                item.getPreco(),
                quantidade,
                item.getSubTotal()
        );
    }
}
