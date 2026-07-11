package com.nativa.horario_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import org.springframework.stereotype.Component;

import com.nativa.horario_service.controller.HorarioControllerV2;
import com.nativa.horario_service.dto.HorarioResponse;


@Component
public class HorarioModelAssembler implements RepresentationModelAssembler<HorarioResponse, EntityModel<HorarioResponse>>{

    @Override
    public EntityModel<HorarioResponse> toModel(HorarioResponse horario){

        return EntityModel.of(horario,
             // Link a sí mismo: GET /pedido/detallepedidos"{id}
            linkTo(methodOn(HorarioControllerV2.class).getHorarioById(horario.getId())).withSelfRel(),
            // Link a la colección completa
            linkTo(methodOn(HorarioControllerV2.class).getAllHorarios()).withRel("Horarios"));
        }
}
