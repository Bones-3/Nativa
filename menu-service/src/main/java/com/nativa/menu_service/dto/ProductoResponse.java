package com.nativa.menu_service.dto;

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
    private String precio;
    private Boolean disponible;
}
