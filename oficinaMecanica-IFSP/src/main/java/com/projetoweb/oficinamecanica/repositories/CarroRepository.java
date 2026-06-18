package com.projetoweb.oficinamecanica.repositories;

import com.projetoweb.oficinamecanica.entities.Carro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarroRepository extends JpaRepository<Carro, Long> {
}
