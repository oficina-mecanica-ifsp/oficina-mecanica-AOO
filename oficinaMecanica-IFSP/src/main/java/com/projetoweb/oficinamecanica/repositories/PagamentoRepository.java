package com.projetoweb.oficinamecanica.repositories;

import com.projetoweb.oficinamecanica.entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    boolean existsByOrderId(Long orderId);

    Optional<Pagamento> findByOrderId(Long orderId);
}
