package com.projetoweb.oficinamecanica.repositories;

import com.projetoweb.oficinamecanica.entities.OrderProduto;
import com.projetoweb.oficinamecanica.entities.pk.OrderProdutoPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProdutoRepository extends JpaRepository<OrderProduto, OrderProdutoPK> {
}
