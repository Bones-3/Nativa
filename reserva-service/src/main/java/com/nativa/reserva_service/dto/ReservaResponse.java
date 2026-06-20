package com.nativa.reserva_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReservaResponse {

    private Long id;
    private String nombreCliente;
    private String telefono;
    private String email;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer cantidadPersonas;
    private String estado;
    private LocalDateTime fechaCreacion;

    private Long mesaId;
    private Integer mesaNumero;
    private Integer mesaCapacidad;
    private String mesaUbicacion;
}
