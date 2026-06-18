package com.projetoweb.oficinamecanica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OficinaMecanicaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OficinaMecanicaApplication.class, args);
    }

}
