--liquibase formatted sql

--changeset Fabián:001
CREATE TABLE promocion (
    id_promocion BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    porcentaje_descuento DECIMAL(5,2) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

--changeset Fabián:002
INSERT INTO promocion (codigo, descripcion, porcentaje_descuento, fecha_inicio, fecha_fin, activo) VALUES
('PROMO25', 'Descuento especial de invierno', 25, '2026-07-10', '2026-07-20', TRUE),
('BIENVENIDA', 'Bono por registro de nuevo usuario', 10, '2026-01-01', '2026-12-31', TRUE),
('CYBER2026', 'Descuento masivo Cyber Day', 50, '2026-11-01', '2026-11-03', TRUE),
('ALMUERZO15', 'Descuento en menú ejecutivo diario', 15, '2026-05-15', '2026-08-15', TRUE),
('FINDE20', 'Promoción válida solo fines de semana', 20, '2026-07-11', '2026-07-13', TRUE);