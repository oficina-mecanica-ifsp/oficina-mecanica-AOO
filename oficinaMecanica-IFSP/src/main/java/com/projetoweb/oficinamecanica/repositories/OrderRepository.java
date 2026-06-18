package com.projetoweb.oficinamecanica.repositories;

import com.projetoweb.oficinamecanica.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"orderServicos", "orderProdutos"})
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findWithItensById(Long id);

    @EntityGraph(attributePaths = {"orderServicos", "orderProdutos"})
    @Query(value = "SELECT DISTINCT o FROM Order o",
           countQuery = "SELECT COUNT(DISTINCT o) FROM Order o")
    Page<Order> findAllWithItens(Pageable pageable);
}
