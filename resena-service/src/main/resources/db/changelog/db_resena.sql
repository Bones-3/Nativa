--liquibase formatted sql

--changeset Fabián:001
CREATE TABLE resena (
    id_resena BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    comentario VARCHAR(255),
    calificacion INT NOT NULL
);

--changeset Fabián:002
INSERT INTO resena (id_producto, id_usuario, comentario, calificacion) VALUES
(1, 3, 'El plato principal estuvo espectacular, la carne en su punto exacto.', 5),
(2, 5, 'Muy sabroso todo, pero los acompañamientos llegaron un poco fríos.', 4),
(5, 1, 'La porción me pareció muy pequeña para el precio que tiene este plato.', 2),
(7, 10, '¡Espectacular! El postre es una delicia, lo pediría mil veces más.', 5),
(10, 2, 'Buen sabor, aunque la presentación del plato podría mejorar.', 3),
(3, 8, 'Mala experiencia, la comida estaba excesivamente salada y incomible.', 1),
(8, 4, 'Relación precio-calidad insuperable. Un menú ejecutivo muy completo.', 4),
(4, 7, 'Un plato promedio, bien sazonado pero nada fuera de lo común.', 3),
(9, 9, 'La entrada estuvo increíble y los ingredientes se notaban muy frescos.', 5),
(6, 6, 'El sabor es aceptable, pero la salsa que venía encima estaba muy ácida.', 3);