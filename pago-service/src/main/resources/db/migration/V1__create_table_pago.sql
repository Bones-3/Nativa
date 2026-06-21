-- Flyway V1: crear tabla pago e insertar datos iniciales
-- Archivo: ms-pago/src/main/resources/db/migration/V1__create_table_pago.sql
 
CREATE TABLE pago (
    id_pago     BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id   BIGINT NOT NULL,
    usuario_id  BIGINT,
    monto       DOUBLE NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    fecha_pago  DATETIME NOT NULL
);
 
INSERT INTO pago (pedido_id, usuario_id, monto, metodo_pago, fecha_pago) VALUES
(1, 1, 15500.00, 'TARJETA_DEBITO',  '2024-01-10 12:00:00'),
(2, 2, 23000.00, 'TARJETA_CREDITO', '2024-01-11 13:30:00'),
(3, 3, 8900.00,  'EFECTIVO',        '2024-01-12 14:00:00'),
(4, 1, 31200.00, 'TARJETA_DEBITO',  '2024-01-13 15:15:00'),
(5, 4, 12400.00, 'TRANSFERENCIA',   '2024-01-14 16:00:00'),
(6, 2, 9800.00,  'EFECTIVO',        '2024-01-15 11:30:00'),
(7, 5, 45000.00, 'TARJETA_CREDITO', '2024-01-16 10:00:00'),
(8, 3, 18700.00, 'TARJETA_DEBITO',  '2024-01-17 12:45:00'),
(9, 6, 7500.00,  'TRANSFERENCIA',   '2024-01-18 09:30:00'),
(10, 1, 22300.00, 'TARJETA_CREDITO','2024-01-19 17:00:00');
 