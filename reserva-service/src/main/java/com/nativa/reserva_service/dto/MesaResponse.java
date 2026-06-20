package com.nativa.reserva_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MesaResponse {

    private Long id;
    private Integer numero;
    private Integer capacidad;
    private String ubicacion;
    private Boolean disponible;
}
