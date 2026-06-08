package com.nativa.menu_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaRequest {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción de la categoría es obligatoria")    
    private String descripcion;
}
