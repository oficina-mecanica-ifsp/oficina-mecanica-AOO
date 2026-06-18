package com.projetoweb.oficinamecanica.repositories;

import com.projetoweb.oficinamecanica.entities.OrderServico;
import com.projetoweb.oficinamecanica.entities.pk.OrderServicoPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderServicoRepository extends JpaRepository<OrderServico, OrderServicoPK> {
}
