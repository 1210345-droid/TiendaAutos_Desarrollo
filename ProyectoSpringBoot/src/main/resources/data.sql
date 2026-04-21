INSERT INTO clientes (nombre, apellido, telefono) VALUES ('El Gran', 'Jefe', '555-ADMIN');
INSERT INTO usuarios_login (usuario, password, rol, id_cliente) VALUES ('admin', 'admin', 'admin', 1);

INSERT INTO marca (descripcion) VALUES ('Ford'), ('Chevrolet'), ('Nissan');
INSERT INTO proveedores (nombre) VALUES ('Autozone'), ('Refaccionaria California');

INSERT INTO productos (descripcion, precio_venta, costo, id_marca, id_proveedor, imagen_url, categoria) 
VALUES ('Amortiguador de Super Lujo', 1200.50, 800.00, 1, 1, 'https://cdn-icons-png.flaticon.com/512/2880/2880290.png', 'Refacciones');
