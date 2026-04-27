package com.sistemaautos.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class SoporteController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<Map<String, Object>> normalizeKeys(List<Map<String, Object>> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : list) {
            Map<String, Object> newMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey().toLowerCase();
                newMap.put(key, entry.getValue());
            }
            result.add(newMap);
        }
        return result;
    }

    @PostMapping("/soporte")
    public Map<String, Object> crearTicket(@RequestBody Map<String, Object> body) {
        Object idC = body.get("idCliente");
        String asunto = (String) body.get("asunto");
        String mensaje = (String) body.get("mensaje");

        jdbcTemplate.update("INSERT INTO soporte_tickets (id_cliente, asunto, mensaje) VALUES (?, ?, ?)", 
                            idC, asunto, mensaje);
                            
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }

    @GetMapping("/admin/soporte")
    public List<Map<String, Object>> getTickets() {
        String query = "SELECT t.id_ticket, t.id_cliente, t.asunto, t.mensaje, t.estado, t.fecha_creacion, c.nombre, c.apellido " +
                       "FROM soporte_tickets t LEFT JOIN clientes c ON t.id_cliente = c.id_cliente " +
                       "ORDER BY t.id_ticket DESC";
        return normalizeKeys(jdbcTemplate.queryForList(query));
    }

    @Transactional
    @PutMapping("/admin/soporte/resolver/{id}")
    public Map<String, Object> resolverTicket(@PathVariable int id) {
        jdbcTemplate.update("UPDATE soporte_tickets SET estado = 'Resuelto' WHERE id_ticket = ?", id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }

    @GetMapping("/mis_tickets/{idCliente}")
    public List<Map<String, Object>> misTickets(@PathVariable int idCliente) {
        String query = "SELECT t.id_ticket, t.asunto, t.mensaje, t.estado, t.fecha_creacion " +
                       "FROM soporte_tickets t WHERE t.id_cliente = ? ORDER BY t.id_ticket DESC";
        return normalizeKeys(jdbcTemplate.queryForList(query, idCliente));
    }
}
