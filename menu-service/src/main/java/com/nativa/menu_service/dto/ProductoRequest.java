package com.nativa.menu_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoRequest {
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La descripción del producto es obligatoria")
    private String descripcion; 
    
    @NotNull(message = "El precio del producto es obligatorio")
    @Positive(message = "El precio del producto no puede ser negativo")
    private BigDecimal precio;
    
    @NotNull(message = "La categoría del producto es obligatoria")
    private Long categoriaId;
}
