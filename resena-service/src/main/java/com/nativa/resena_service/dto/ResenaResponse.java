package com.nativa.resena_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResenaResponse {
    private Long id;
    private Long productoId;
    private Long usuarioId;
    private String comentario;
    private Integer calificacion;

}
