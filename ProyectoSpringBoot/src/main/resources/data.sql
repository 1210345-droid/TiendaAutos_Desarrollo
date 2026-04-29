INSERT INTO marca (descripcion) VALUES ('Ford'), ('Chevrolet'), ('Nissan'), ('Pirelli'), ('Brembo');
INSERT INTO proveedores (nombre) VALUES ('Autozone'), ('Refaccionaria California');

INSERT INTO productos (descripcion, precio_venta, costo, id_marca, id_proveedor, categoria) VALUES 
('Amortiguador de Lujo', 1200.50, 800.00, 1, 1, 'Refacciones'),
('Batería 12V LTH', 2500.00, 1800.00, 2, 2, 'Eléctrico'),
('Llanta Pirelli 205/55 R16', 1500.00, 1100.00, 4, 1, 'Llantas'),
('Balatas Delanteras', 600.00, 350.00, 5, 2, 'Frenos'),
('Filtro de Aceite', 150.00, 80.00, 1, 1, 'Mantenimiento'),
('Aceite Sintético 5W-30', 800.00, 500.00, 3, 2, 'Mantenimiento'),
('Bujías Iridium (Set 4)', 450.00, 250.00, 3, 1, 'Eléctrico'),
('Bomba de Agua', 1800.00, 1200.00, 2, 1, 'Refacciones'),
('Radiador de Aluminio', 3200.00, 2100.00, 1, 2, 'Refacciones'),
('Juego de Tapetes', 500.00, 300.00, 1, 1, 'Interiores');
