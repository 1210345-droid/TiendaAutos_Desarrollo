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
public class CheckoutLogisticaController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<Map<String, Object>> normalizeKeys(List<Map<String, Object>> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : list) {
            Map<String, Object> newMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                newMap.put(entry.getKey().toLowerCase(), entry.getValue());
            }
            result.add(newMap);
        }
        return result;
    }

    // ================= CHECKOUT =================
    @Transactional
    @SuppressWarnings("unchecked")
    @PostMapping("/checkout")
    public Map<String, Object> checkout(@RequestBody Map<String, Object> body) {
        Object idC = body.get("idCliente");
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

        double total = 0;
        for (Map<String, Object> it : items) {
            double p = Double.parseDouble(it.get("precioVenta").toString());
            int c = Integer.parseInt(it.get("cantidad").toString());
            total += (p * c);
        }

        jdbcTemplate.update("INSERT INTO orden_venta (id_cliente, fecha_orden, status_orden, total_orden) VALUES (?, CURRENT_TIMESTAMP, 'Procesando Pago', ?)", 
                            idC, total);
        Integer idOrden = jdbcTemplate.queryForObject("SELECT SCOPE_IDENTITY()", Integer.class);

        for (Map<String, Object> it : items) {
            double p = Double.parseDouble(it.get("precioVenta").toString());
            int c = Integer.parseInt(it.get("cantidad").toString());
            
            jdbcTemplate.update("INSERT INTO detalle_orden (id_orden, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)", 
                                idOrden, it.get("id"), c, p, p * c);
        }

        Map<String, Object> res = new HashMap<>(); 
        res.put("success", true);
        res.put("id_orden", idOrden);
        return res;
    }

    // ================= HISTORIAL Y TRACKING =================
    @SuppressWarnings("unchecked")
    @GetMapping("/mis_ordenes/{idCliente}")
    public List<Map<String, Object>> misOrdenes(@PathVariable int idCliente) {
        String q = "SELECT o.id_orden, o.fecha_orden, o.total_orden, o.status_orden, " +
                   "d.cantidad, d.precio_unitario, p.descripcion " +
                   "FROM orden_venta o INNER JOIN detalle_orden d ON o.id_orden = d.id_orden " +
                   "INNER JOIN productos p ON d.id_producto = p.id_producto " +
                   "WHERE o.id_cliente = ? ORDER BY o.fecha_orden DESC";
        
        List<Map<String, Object>> rows = normalizeKeys(jdbcTemplate.queryForList(q, idCliente));
        Map<Integer, Map<String, Object>> map = new HashMap<>();

        for (Map<String, Object> r : rows) {
            int ido = (int) r.get("id_orden");
            if (!map.containsKey(ido)) {
                Map<String, Object> obj = new HashMap<>();
                obj.put("id_orden", ido);
                obj.put("fecha", r.get("fecha_orden"));
                obj.put("total", r.get("total_orden"));
                obj.put("status", r.get("status_orden"));
                obj.put("items", new ArrayList<Map<String,Object>>());
                map.put(ido, obj);
            }
            Map<String, Object> it = new HashMap<>();
            it.put("desc", r.get("descripcion"));
            it.put("qty", r.get("cantidad"));
            it.put("price", r.get("precio_unitario"));
            ((List<Map<String,Object>>) map.get(ido).get("items")).add(it);
        }
        return new ArrayList<>(map.values());
    }

    @GetMapping("/admin/ordenes")
    public List<Map<String, Object>> getTodasOrdenes() {
        return normalizeKeys(jdbcTemplate.queryForList(
            "SELECT id_orden, total_orden, status_orden, fecha_orden " +
            "FROM orden_venta ORDER BY fecha_orden DESC"
        ));
    }

    @PutMapping("/admin/orden_status/{id}")
    public Map<String, Object> updStatus(@PathVariable int id, @RequestBody Map<String, String> body) {
        jdbcTemplate.update("UPDATE orden_venta SET status_orden = ? WHERE id_orden = ?", body.get("status"), id);
        Map<String, Object> res = new HashMap<>(); res.put("success", true); return res;
    }
}
