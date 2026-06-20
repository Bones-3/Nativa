--liquibase formatted sql

--changeset Fabián:001
CREATE TABLE mesa (
    id_mesa BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    capacidad INT NOT NULL,
    ubicacion VARCHAR(60),
    disponible BOOLEAN NOT NULL DEFAULT TRUE
);

--changeset Fabián:002
CREATE TABLE reserva (
    id_reserva BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_cliente VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    cantidad_personas INT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mesa_id BIGINT NOT NULL,
    FOREIGN KEY (mesa_id) REFERENCES mesa(id_mesa)
);

--changeset Fabián:003
INSERT INTO mesa (numero, capacidad, ubicacion, disponible) VALUES
(1, 2, 'Interior', true),
(2, 4, 'Interior', true),
(3, 4, 'Interior', true),
(4, 6, 'Interior', true),
(5, 8, 'Interior', true),
(6, 2, 'Terraza', true),
(7, 4, 'Terraza', true),
(8, 6, 'Terraza', true),
(9, 2, 'Barra', true),
(10, 1, 'Barra', true);
