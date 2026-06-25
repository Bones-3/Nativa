--liquibase formatted sql

--changeset fabian:1
CREATE TABLE pedidos (
    id_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    fecha_pedido DATETIME NOT NULL,
    tipo_entrega VARCHAR(50) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    iva DECIMAL(12,2) NOT NULL,
    total_pagar DECIMAL(12,2) NOT NULL
);

--changeset fabian:2
CREATE TABLE detalle_pedido (
    id_detalle_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedidos(id_pedido)
);

--changeset fabian:3
INSERT INTO pedidos (usuario_id, fecha_pedido, tipo_entrega, subtotal, iva, total_pagar) VALUES
(1,'2026-01-01 10:00:00','DELIVERY',0,0,0),
(2,'2026-01-02 11:00:00','RETIRO',0,0,0),
(3,'2026-01-03 12:00:00','DELIVERY',0,0,0),
(4,'2026-01-04 13:00:00','RETIRO',0,0,0),
(5,'2026-01-05 14:00:00','DELIVERY',0,0,0),
(6,'2026-01-06 15:00:00','RETIRO',0,0,0),
(7,'2026-01-07 16:00:00','DELIVERY',0,0,0),
(8,'2026-01-08 17:00:00','RETIRO',0,0,0),
(9,'2026-01-09 18:00:00','DELIVERY',0,0,0),
(10,'2026-01-10 19:00:00','RETIRO',0,0,0);