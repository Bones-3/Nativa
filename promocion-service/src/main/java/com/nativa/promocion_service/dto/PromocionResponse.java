package com.nativa.promocion_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PromocionResponse {
    private Long id;
    private String codigo;
    private String descripcion;
    private BigDecimal porcentajeDescuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
}
