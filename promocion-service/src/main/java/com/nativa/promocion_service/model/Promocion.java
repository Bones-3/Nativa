package com.nativa.promocion_service.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "promocion")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Promocion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promocion")
    private Long id;
    
    @Column(name = "codigo")
    private String codigo;
    
    @Column(name = "descripcion", length = 255)
    private String descripcion;
    
    @Column(name = "porcentaje_descuento")
    private BigDecimal porcentajeDescuento;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}