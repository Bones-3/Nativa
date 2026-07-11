--liquibase formatted sql

--changeset Fabián:001
CREATE TABLE horario (
    id_horario BIGINT AUTO_INCREMENT PRIMARY KEY,
    dia_semana VARCHAR(15) NOT NULL UNIQUE,
    hora_apertura TIME,
    hora_cierre TIME,
    cerrado BOOLEAN NOT NULL DEFAULT FALSE
);
