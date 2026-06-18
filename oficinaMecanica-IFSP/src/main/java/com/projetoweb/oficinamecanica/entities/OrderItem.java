package com.projetoweb.oficinamecanica.entities;

import java.math.BigDecimal;

/**
 * Contrato comum para itens de uma Ordem de Serviço.
 * Implementado por OrderProduto e OrderServico, permitindo
 * que Order.getItens() retorne uma lista tipada em vez de List<Object>.
 */
public interface OrderItem {
    String getNome();
    BigDecimal getPreco();
    String getTipo();
    BigDecimal getSubTotal();
}