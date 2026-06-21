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

--changeset Fabián:004
INSERT INTO producto (nombre, descripcion, precio, disponible, categoria_id) VALUES 
('Empanadas de Pino', 'Dos empanadas horneadas rellenas de carne, cebolla, huevo y aceituna', 4500, true, 1),
('Lomo a lo Pobre', 'Bife de lomo liso acompañado de papas fritas, cebolla frita y dos huevos fritos', 12900, true, 2),
('Pastel de Choclo', 'Tradicional pastel al horno en paila de greda con pino de carne y pollo', 9500, true, 2),
('Papas Fritas Familiares', 'Porción grande de papas fritas rústicas crujientes', 5500, true, 3),
('Arroz Chaufa', 'Porción de arroz saltado con cebollín y pimentón', 3200, true, 3),
('Mote con Huesillo', 'Refrescante postre tradicional con dos huesillos y mote de trigo', 3800, true, 4),
('Bebida en Lata 350cc', 'Coca-Cola, Sprite o Fanta helada', 1800, true, 5),
('Pisco Sour Catedral', 'Receta de la casa con pisco peruano y limón sutil', 5200, true, 5),
('Churrasco Italiano', 'Finas láminas de carne, palta molida, tomate y mayonesa casera en pan frica', 7900, true, 7),
('Porción de Palta', 'Agregado de palta extra molida para tus platos o sánguches', 1500, true, 8);