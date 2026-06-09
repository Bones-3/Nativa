package com.nativa.menu_service.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoRequest {
    private String nombre;
    private String descripcion; 
    private BigDecimal precio;
}
