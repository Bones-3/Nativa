package com.nativa.reserva_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.reserva_service.dto.MesaRequest;
import com.nativa.reserva_service.dto.MesaResponse;
import com.nativa.reserva_service.model.Mesa;

@Component
public class MesaMapper {

    public Mesa toEntity(MesaRequest request) {
        Mesa mesa = new Mesa();
        mesa.setNumero(request.getNumero());
        mesa.setCapacidad(request.getCapacidad());
        mesa.setUbicacion(request.getUbicacion());
        mesa.setDisponible(true);
        return mesa;
    }

    public MesaResponse toResponse(Mesa mesa) {
        return MesaResponse.builder()
                .id(mesa.getId())
                .numero(mesa.getNumero())
                .capacidad(mesa.getCapacidad())
                .ubicacion(mesa.getUbicacion())
                .disponible(mesa.getDisponible())
                .build();
    }
}
