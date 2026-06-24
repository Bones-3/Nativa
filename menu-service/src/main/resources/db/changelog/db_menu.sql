--liquibase formatted sql

--changeset Fabián:001
CREATE TABLE categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE
);

--changeset Fabián:002
CREATE TABLE producto (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    precio NUMERIC(10,0) NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    categoria_id BIGINT NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id_categoria)
);

--changeset Fabián:003
INSERT INTO categoria (nombre, disponible) VALUES 
('Entradas', true),
('Platos de Fondo', true),
('Acompañamientos', true),
('Postres', true),
('Bebestibles', true),
('Cafetería', true),
('Sánguches', true),
('Agregados', true);
