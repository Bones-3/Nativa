package com.nativa.resena_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResenaRequest {
    @NotNull(message = "El id del producto no puede estar vacio, ingresa el id")
    private Long productoId;
    @NotNull(message = "La id usuario no puede estar vacia, ingresa la id")
    private Long usuarioId;
    @NotBlank(message = "Dejanos un comentario sobre el servicio")
    private String comentario;
    @NotNull(message = "Califica el servicio del 1 al 5")
    private Integer calificacion;

}
