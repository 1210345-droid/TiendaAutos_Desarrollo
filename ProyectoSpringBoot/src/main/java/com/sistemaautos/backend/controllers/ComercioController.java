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
public class ComercioController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ================= CLIENTES =================
    @GetMapping("/clientes")
    public List<Map<String, Object>> getClientes() {
        return jdbcTemplate.queryForList("SELECT id_cliente as id, nombre, apellido, telefono FROM clientes");
    }

    @PostMapping("/clientes")
    public Map<String, Object> addCliente(@RequestBody Map<String, String> body) {
        jdbcTemplate.update("INSERT INTO clientes (nombre, apellido, telefono) VALUES (?, ?, ?)", 
                            body.get("nombre"), body.get("apellido"), body.get("telefono"));
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }

    @DeleteMapping("/clientes/{id}")
    public Map<String, Object> delCliente(@PathVariable int id) {
        jdbcTemplate.update("DELETE FROM clientes WHERE id_cliente = ?", id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }

    // ================= MARCAS Y PROVS =================
    @GetMapping("/marcas")
    public List<Map<String, Object>> getMarcas() {
        return jdbcTemplate.queryForList("SELECT id_marca as id, descripcion as nombre FROM marca");
    }

    @GetMapping("/proveedores")
    public List<Map<String, Object>> getProveedores() {
        return jdbcTemplate.queryForList("SELECT id_proveedor as id, nombre FROM proveedores");
    }

    // ================= PRODUCTOS =================
    @GetMapping("/productos")
    public List<Map<String, Object>> getProductos() {
        String query = "SELECT p.id_producto as id, m.descripcion as marca, pr.nombre as proveedor, " +
                       "p.descripcion, p.costo, p.precio_venta as precioVenta, p.imagen_url, p.categoria, " +
                       "p.condicion_uso, p.detalles_producto, c.nombre as vendedor_nombre, c.apellido as vendedor_apellido " +
                       "FROM productos p LEFT JOIN marca m ON p.id_marca = m.id_marca " +
                       "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                       "LEFT JOIN clientes c ON p.id_vendedor = c.id_cliente ORDER BY p.id_producto DESC";
        return jdbcTemplate.queryForList(query);
    }

    @PostMapping("/productos")
    public Map<String, Object> addProducto(@RequestBody Map<String, Object> body) {
        String desc = (String) body.get("descripcion");
        Object pv = body.get("precioVenta");
        Object c = body.get("costo");
        String marca = (String) body.get("nombreMarca");
        String prov = (String) body.get("nombreProv");
        String img = (String) body.get("imagen_url");
        String cat = (String) body.get("categoria");

        Integer idM = null;
        if (marca != null && !marca.isEmpty()) {
            List<Map<String, Object>> mRes = jdbcTemplate.queryForList("SELECT id_marca FROM marca WHERE descripcion = ?", marca);
            if (!mRes.isEmpty()) idM = (Integer) mRes.get(0).get("id_marca");
            else {
                jdbcTemplate.update("INSERT INTO marca (descripcion) VALUES (?)", marca);
                idM = jdbcTemplate.queryForObject("SELECT SCOPE_IDENTITY()", Integer.class);
            }
        }

        Integer idP = null;
        if (prov != null && !prov.isEmpty()) {
            List<Map<String, Object>> pRes = jdbcTemplate.queryForList("SELECT id_proveedor FROM proveedores WHERE nombre = ?", prov);
            if (!pRes.isEmpty()) idP = (Integer) pRes.get(0).get("id_proveedor");
            else {
                jdbcTemplate.update("INSERT INTO proveedores (nombre) VALUES (?)", prov);
                idP = jdbcTemplate.queryForObject("SELECT SCOPE_IDENTITY()", Integer.class);
            }
        }

        jdbcTemplate.update("INSERT INTO productos (descripcion, precio_venta, costo, id_marca, id_proveedor, imagen_url, categoria) VALUES (?, ?, ?, ?, ?, ?, ?)", 
                            desc, pv, c != null ? c : 0, idM, idP, img, cat != null ? cat : "General");
        
        Map<String, Object> res = new HashMap<>(); res.put("success", true); return res;
    }

    @PutMapping("/productos/{id}")
    public Map<String, Object> updProducto(@PathVariable int id, @RequestBody Map<String, Object> body) {
        String desc = (String) body.get("descripcion");
        Object pv = body.get("precioVenta");
        Object c = body.get("costo");
        String marca = (String) body.get("nombreMarca");
        String prov = (String) body.get("nombreProv");
        String img = (String) body.get("imagen_url");
        String cat = (String) body.get("categoria");

        Integer idM = null; Integer idP = null;
        
        if (marca != null && !marca.isEmpty()) {
            List<Map<String, Object>> mRes = jdbcTemplate.queryForList("SELECT id_marca FROM marca WHERE descripcion = ?", marca);
            if (!mRes.isEmpty()) idM = (Integer) mRes.get(0).get("id_marca");
            else {
                jdbcTemplate.update("INSERT INTO marca (descripcion) VALUES (?)", marca);
                idM = jdbcTemplate.queryForObject("SELECT SCOPE_IDENTITY()", Integer.class);
            }
        }
        if (prov != null && !prov.isEmpty()) {
            List<Map<String, Object>> pRes = jdbcTemplate.queryForList("SELECT id_proveedor FROM proveedores WHERE nombre = ?", prov);
            if (!pRes.isEmpty()) idP = (Integer) pRes.get(0).get("id_proveedor");
            else {
                jdbcTemplate.update("INSERT INTO proveedores (nombre) VALUES (?)", prov);
                idP = jdbcTemplate.queryForObject("SELECT SCOPE_IDENTITY()", Integer.class);
            }
        }

        jdbcTemplate.update("UPDATE productos SET descripcion=?, precio_venta=?, costo=?, id_marca=?, id_proveedor=?, imagen_url=?, categoria=? WHERE id_producto=?", 
                            desc, pv, c != null ? c : 0, idM, idP, img, cat != null ? cat : "General", id);
        Map<String, Object> res = new HashMap<>(); res.put("success", true); return res;
    }

    @DeleteMapping("/productos/{id}")
    public Map<String, Object> delProducto(@PathVariable int id) {
        jdbcTemplate.update("DELETE FROM productos WHERE id_producto = ?", id);
        Map<String, Object> res = new HashMap<>(); res.put("success", true); return res;
    }

    // ================= PRODUCTOS C2C =================
    @PostMapping("/mis_productos")
    public Map<String, Object> addMisProductos(@RequestBody Map<String, Object> body) {
        jdbcTemplate.update("INSERT INTO productos (id_vendedor, descripcion, precio_venta, imagen_url, categoria, condicion_uso, detalles_producto) VALUES (?, ?, ?, ?, ?, ?, ?)", 
                            body.get("idCliente"), body.get("descripcion"), body.get("precioVenta"), 
                            body.get("imagen_url"), body.get("categoria"), body.get("condicion_uso"), body.get("detalles_producto"));
        Map<String, Object> res = new HashMap<>(); res.put("success", true); return res;
    }
}
