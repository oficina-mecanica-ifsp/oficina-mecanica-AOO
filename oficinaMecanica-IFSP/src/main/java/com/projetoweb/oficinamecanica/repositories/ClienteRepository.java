package com.projetoweb.oficinamecanica.repositories;

import com.projetoweb.oficinamecanica.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c from Cliente c where c.doc = :doc")
    Optional<Cliente> findByDoc(String doc);
}
