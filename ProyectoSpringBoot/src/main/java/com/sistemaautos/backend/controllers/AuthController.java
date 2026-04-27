package com.sistemaautos.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        
        List<Map<String, Object>> result = jdbcTemplate.queryForList(
            "SELECT id, usuario, rol FROM usuarios_login WHERE usuario = ? AND clave = ?", 
            username, password
        );

        Map<String, Object> res = new HashMap<>();
        if (!result.isEmpty()) {
            res.put("success", true);
            res.put("message", "Login exitoso");
            res.put("rol", result.get(0).getOrDefault("rol", result.get(0).get("ROL")));
        } else {
            res.put("success", false);
            res.put("message", "Credenciales incorrectas");
        }
        return res;
    }

    @Transactional
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String nombre = body.get("nombre");
        String apellido = body.get("apellido");
        String telefono = body.get("telefono");
        
        Map<String, Object> res = new HashMap<>();
        
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM usuarios_login WHERE usuario = ?", Integer.class, username);
        if (count != null && count > 0) {
            res.put("success", false);
            res.put("message", "Este usuario ya existe");
            return res;
        }

        jdbcTemplate.update("INSERT INTO clientes (nombre, apellido, telefono) VALUES (?, ?, ?)", nombre, apellido, telefono != null ? telefono : "");
        Integer idCliente = jdbcTemplate.queryForObject("SELECT SCOPE_IDENTITY()", Integer.class);

        jdbcTemplate.update("INSERT INTO usuarios_login (usuario, clave, rol, id_cliente) VALUES (?, ?, 'cliente', ?)", username, password, idCliente);

        res.put("success", true);
        res.put("message", "Registro y Perfil atado de cliente fue exitoso");
        return res;
    }

    @GetMapping("/perfil/{username}")
    public Map<String, Object> getPerfil(@PathVariable String username) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList(
            "SELECT c.id_cliente as id, c.nombre, c.apellido, c.telefono, c.direccion " +
            "FROM usuarios_login u " +
            "INNER JOIN clientes c ON u.id_cliente = c.id_cliente " +
            "WHERE u.usuario = ?", username
        );
        return result.isEmpty() ? new HashMap<>() : result.get(0);
    }
}
