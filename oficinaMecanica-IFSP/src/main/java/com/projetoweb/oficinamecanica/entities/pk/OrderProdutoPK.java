package com.projetoweb.oficinamecanica.entities.pk;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderProdutoPK implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long produtoId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderProdutoPK that = (OrderProdutoPK) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(produtoId, that.produtoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, produtoId);
    }
}
