package com.sistemaautos.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Aplicacion {

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
        System.out.println("🚀 Servidor Spring Boot LocalDB Inicializado Con Éxito 🚀");
    }

}
