package com.projetoweb.oficinamecanica.repositories;

import com.projetoweb.oficinamecanica.entities.Produto;
import com.projetoweb.oficinamecanica.entities.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
}
