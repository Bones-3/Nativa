package com.nativa.menu_service.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion; 
    private BigDecimal precio;
    private Boolean disponible;

    private Long categoriaId;
    private String categoriaNombre;
}
