package com.nativa.reserva_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.reserva_service.dto.ReservaRequest;
import com.nativa.reserva_service.dto.ReservaResponse;
import com.nativa.reserva_service.model.Reserva;

@Component
public class ReservaMapper {

    public Reserva toEntity(ReservaRequest request) {
        Reserva reserva = new Reserva();
        reserva.setNombreCliente(request.getNombreCliente());
        reserva.setTelefono(request.getTelefono());
        reserva.setEmail(request.getEmail());
        reserva.setFecha(request.getFecha());
        reserva.setHora(request.getHora());
        reserva.setCantidadPersonas(request.getCantidadPersonas());
        return reserva;
    }

    public ReservaResponse toResponse(Reserva reserva) {
        return ReservaResponse.builder()
                .id(reserva.getId())
                .nombreCliente(reserva.getNombreCliente())
                .telefono(reserva.getTelefono())
                .email(reserva.getEmail())
                .fecha(reserva.getFecha())
                .hora(reserva.getHora())
                .cantidadPersonas(reserva.getCantidadPersonas())
                .estado(reserva.getEstado())
                .fechaCreacion(reserva.getFechaCreacion())
                .mesaId(reserva.getMesa().getId())
                .mesaNumero(reserva.getMesa().getNumero())
                .mesaCapacidad(reserva.getMesa().getCapacidad())
                .mesaUbicacion(reserva.getMesa().getUbicacion())
                .build();
    }
}
