package com.nativa.menu_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoriaResponse {
    
    private Long id;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción de la categoría no puede estar vacía")
    private String descripcion;

    
    private Boolean disponible;
}
