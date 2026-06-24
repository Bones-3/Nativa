package com.nativa.resena_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resenas")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Long id;
    @Column(name = "id_producto")
    private Long productoId;
    @Column(name = "id_usuario")
    private Long usuarioId;
    @Column(name = "comentario")
    private String comentario;
    @Column(name = "calificacion")
    private Integer calificacion;
}
