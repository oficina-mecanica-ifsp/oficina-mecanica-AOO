package com.projetoweb.oficinamecanica.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projetoweb.oficinamecanica.entities.pk.OrderServicoPK;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

@Entity
@Table(name = "tb_order_servico")
public class OrderServico implements Serializable, OrderItem {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private OrderServicoPK id = new OrderServicoPK();

    private String nome;

    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    private String descricao;

    @JsonFormat(pattern = "HH:mm")
    private Duration duracao;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne
    @MapsId("servicoId")
    @JoinColumn(name = "servico_id")
    @JsonIgnore
    private Servico servico;

    public OrderServico() {
    }

    public OrderServico(Order order, Servico servico, String nome, BigDecimal preco, String descricao, Duration duracao) {
        this.order = order;
        this.servico = servico;

        this.id.setOrderId(order.getId());
        this.id.setServicoId(servico.getId());

        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.duracao = duracao;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.id.setOrderId(order.getId());
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
        this.id.setServicoId(servico.getId());
    }

    public OrderServicoPK getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Duration getDuracao() {
        return duracao;
    }

    public void setDuracao(Duration duracao) {
        this.duracao = duracao;
    }

    @Override
    public BigDecimal getSubTotal() {
        return preco != null ? preco : BigDecimal.ZERO;
    }

    @Override
    public String getTipo() {
        return "SERVICO";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderServico that = (OrderServico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
