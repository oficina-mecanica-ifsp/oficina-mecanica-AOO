package com.projetoweb.oficinamecanica.repositories;

import com.projetoweb.oficinamecanica.entities.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("SELECT p FROM Produto p WHERE p.quantidadeMinima IS NOT NULL AND p.quantidade IS NOT NULL AND p.quantidade <= p.quantidadeMinima")
    Page<Produto> findEstoqueCritico(Pageable pageable);
}
