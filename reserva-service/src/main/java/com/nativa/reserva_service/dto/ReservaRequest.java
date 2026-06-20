package com.nativa.reserva_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservaRequest {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    private String telefono;

    private String email;

    @NotNull(message = "La fecha de la reserva es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora de la reserva es obligatoria")
    private LocalTime hora;

    @NotNull(message = "La cantidad de personas es obligatoria")
    @Positive(message = "La cantidad de personas debe ser positiva")
    private Integer cantidadPersonas;

    @NotNull(message = "La mesa es obligatoria")
    private Long mesaId;
}
