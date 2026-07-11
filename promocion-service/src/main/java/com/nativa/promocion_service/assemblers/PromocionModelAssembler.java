package com.nativa.promocion_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import org.springframework.stereotype.Component;
import com.nativa.promocion_service.controller.PromocionControllerV2;
import com.nativa.promocion_service.dto.PromocionResponse;

@Component
public class PromocionModelAssembler implements RepresentationModelAssembler<PromocionResponse, EntityModel<PromocionResponse>>{

    @Override
    public EntityModel<PromocionResponse> toModel(PromocionResponse promocion){

        return EntityModel.of(promocion,
             // Link a sí mismo: GET /pedido/detallepedidos"{id}
            linkTo(methodOn(PromocionControllerV2.class).getPromocionById(promocion.getId())).withSelfRel(),
            // Link a la colección completa
            linkTo(methodOn(PromocionControllerV2.class).getAllPromociones()).withRel("Promociones"));
        }
}
