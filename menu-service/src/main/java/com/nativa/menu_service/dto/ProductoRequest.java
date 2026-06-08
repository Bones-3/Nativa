package com.nativa.menu_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoRequest {
    private String nombre;
    private String descripcion; 
    private String precio;
}
