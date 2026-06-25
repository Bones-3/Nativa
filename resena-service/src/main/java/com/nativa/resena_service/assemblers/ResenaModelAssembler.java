package com.nativa.resena_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import org.springframework.stereotype.Component;
import com.nativa.resena_service.controller.ResenaController;
import com.nativa.resena_service.dto.ResenaResponse;




@Component
public class ResenaModelAssembler implements RepresentationModelAssembler<ResenaResponse, EntityModel<ResenaResponse>>{

    @Override
    public EntityModel<ResenaResponse> toModel(ResenaResponse resena){

        return EntityModel.of(resena,
             // Link a sí mismo: GET /pedido/detallepedidos"{id}
            linkTo(methodOn(ResenaController.class).getResenaById(resena.getId())).withSelfRel(),
            // Link a la colección completa
            linkTo(methodOn(ResenaController.class).getAllResenas()).withRel("Resenas"));
        }
}
