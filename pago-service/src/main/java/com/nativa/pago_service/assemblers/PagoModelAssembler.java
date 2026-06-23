package com.nativa.pago_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.nativa.pago_service.controller.PagoController;
import com.nativa.pago_service.dto.PagoResponse;

@Component
public class PagoModelAssembler implements RepresentationModelAssembler<PagoResponse, EntityModel<PagoResponse>>{
    
    @Override
    public EntityModel<PagoResponse> toModel(PagoResponse pago) {
        return EntityModel.of(pago,
            // Link a sí mismo: GET /menu/productos/{id}
            linkTo(methodOn(PagoController.class).getById(pago.getId())).withSelfRel(),
            // Link a la colección completa
            linkTo(methodOn(PagoController.class).getAllPagos()).withRel("pagos"));
    }
}
