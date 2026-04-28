package com.sistemaautos.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class Aplicacion {

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
        System.out.println("🚀 Servidor Spring Boot LocalDB Inicializado Con Éxito 🚀");
    }

    @Bean
    CommandLineRunner initAdmin(JdbcTemplate jdbc) {
        return args -> {
            try {
                if (jdbc.queryForObject("SELECT COUNT(*) FROM usuarios_login WHERE usuario = 'admin'", Integer.class) == 0) {
                    jdbc.update("INSERT INTO clientes (nombre, apellido, telefono) VALUES ('El Gran', 'Jefe', '555-ADMIN')");
                    Integer maxId = jdbc.queryForObject("SELECT MAX(id_cliente) FROM clientes", Integer.class);
                    jdbc.update("INSERT INTO usuarios_login (usuario, clave, rol, id_cliente) VALUES ('admin', 'admin', 'admin', ?)", maxId);
                }
            } catch (Exception e) {}
        };
    }
}
