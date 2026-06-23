--liquibase formatted sql

--changeset fabian:1
CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    correo_usuario VARCHAR(90) UNIQUE,
    nombres VARCHAR(90),
    apellidos VARCHAR(90),
    telefono_usuario VARCHAR(30),
    estado_usuario BOOLEAN DEFAULT TRUE
);

--changeset fabian:2
INSERT INTO usuario (
    correo_usuario,
    nombres,
    apellidos,
    telefono_usuario,
    estado_usuario
) VALUES
('juan.perez@gmail.com', 'Juan', 'Pérez', '+56911111111', TRUE),
('maria.gonzalez@gmail.com', 'María', 'González', '+56922222222', TRUE),
('carlos.rojas@gmail.com', 'Carlos', 'Rojas', '+56933333333', TRUE),
('ana.morales@gmail.com', 'Ana', 'Morales', '+56944444444', FALSE),
('pedro.silva@gmail.com', 'Pedro', 'Silva', '+56955555555', TRUE),
('camila.torres@gmail.com', 'Camila', 'Torres', '+56966666666', TRUE),
('diego.fuentes@gmail.com', 'Diego', 'Fuentes', '+56977777777', FALSE),
('valentina.lopez@gmail.com', 'Valentina', 'López', '+56988888888', TRUE),
('sebastian.araya@gmail.com', 'Sebastián', 'Araya', '+56999999999', TRUE),
('fernanda.herrera@gmail.com', 'Fernanda', 'Herrera', '+56912345678', TRUE);