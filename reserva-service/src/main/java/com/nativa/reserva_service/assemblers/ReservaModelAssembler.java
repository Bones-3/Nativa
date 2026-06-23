package com.nativa.reserva_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import com.nativa.reserva_service.controller.ReservaController;
import com.nativa.reserva_service.dto.ReservaResponse;

public class ReservaModelAssembler implements RepresentationModelAssembler<ReservaResponse, EntityModel<ReservaResponse>>{

    @Override
    public EntityModel<ReservaResponse> toModel(ReservaResponse reserva) {
        return EntityModel.of(reserva,
            // Link a sí mismo: GET /pedido/detallepedidos"{id}
            linkTo(methodOn(ReservaController.class).getReservaById(reserva.getId())).withSelfRel(),
            // Link a la colección completa
            linkTo(methodOn(ReservaController.class).getAllReservas()).withRel("Reservas"));
    }


}
