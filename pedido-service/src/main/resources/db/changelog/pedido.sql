--liquibase formatted sql
 
--changeset fabian:1
CREATE TABLE pedido (
    id_pedido    BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id   BIGINT NOT NULL,
    fecha_pedido DATETIME NOT NULL,
    total        DOUBLE NOT NULL DEFAULT 0,
    tipo_entrega VARCHAR(50) NOT NULL
);
 
--changeset fabian:2
CREATE TABLE detalle_pedido (
    id_detalle_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id         BIGINT NOT NULL,
    producto_id       BIGINT NOT NULL,
    cantidad          INT NOT NULL,
    precio_unitario   DOUBLE NOT NULL,
    subtotal          DOUBLE NOT NULL,
    notas_pedido      VARCHAR(255),
    CONSTRAINT fk_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id_pedido)
);
 
--changeset fabian:3
INSERT INTO pedido (usuario_id, fecha_pedido, total, tipo_entrega) VALUES
(1, '2024-01-10 11:50:00', 15500.00, 'DELIVERY'),
(2, '2024-01-11 13:20:00', 23000.00, 'RETIRO'),
(3, '2024-01-12 13:50:00', 8900.00,  'DELIVERY'),
(1, '2024-01-13 15:00:00', 31200.00, 'RETIRO'),
(4, '2024-01-14 15:50:00', 12400.00, 'DELIVERY'),
(2, '2024-01-15 11:20:00', 9800.00,  'RETIRO'),
(5, '2024-01-16 09:50:00', 45000.00, 'DELIVERY'),
(3, '2024-01-17 12:35:00', 18700.00, 'RETIRO'),
(6, '2024-01-18 09:20:00', 7500.00,  'DELIVERY'),
(1, '2024-01-19 16:50:00', 22300.00, 'RETIRO');
 
--changeset fabian:4
INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal, notas_pedido) VALUES
(1, 1, 2,  7750.00, 15500.00, NULL),
(2, 3, 1, 23000.00, 23000.00, 'Sin sal'),
(3, 2, 2,  4450.00,  8900.00, 'Extra salsa'),
(4, 4, 3, 10400.00, 31200.00, NULL),
(5, 1, 1, 12400.00, 12400.00, 'Bien cocido'),
(6, 5, 2,  4900.00,  9800.00, NULL),
(7, 2, 5,  9000.00, 45000.00, 'Sin cebolla'),
(8, 3, 2,  9350.00, 18700.00, NULL),
(9, 1, 1,  7500.00,  7500.00, 'Para llevar'),
(10, 4, 2, 11150.00, 22300.00, NULL);