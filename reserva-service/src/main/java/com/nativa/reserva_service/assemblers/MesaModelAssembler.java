package com.nativa.reserva_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.nativa.reserva_service.controller.MesaController;
import com.nativa.reserva_service.dto.MesaResponse;

@Component
public class MesaModelAssembler implements RepresentationModelAssembler<MesaResponse, EntityModel<MesaResponse>> {
     
    
    @Override
    public EntityModel<MesaResponse> toModel(MesaResponse mesa) {
        return EntityModel.of(mesa,
            // Link a sí mismo: GET /pedido/detallepedidos"{id}
            linkTo(methodOn(MesaController.class).getMesaById(mesa.getId())).withSelfRel(),
            // Link a la colección completa
            linkTo(methodOn(MesaController.class).getAllMesas()).withRel("Mesas"),
            // Link a solo la lista de detalles de un pedido
            linkTo(methodOn(MesaController.class).getMesasDisponibles()).withRel("detalles por pedido"));
    }


}
