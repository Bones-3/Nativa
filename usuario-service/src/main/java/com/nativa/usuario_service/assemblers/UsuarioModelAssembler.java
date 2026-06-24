package com.nativa.usuario_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.nativa.usuario_service.controller.UsuarioController;
import com.nativa.usuario_service.dto.UsuarioResponse;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<UsuarioResponse, EntityModel<UsuarioResponse>>{
    
    @Override
    public EntityModel<UsuarioResponse> toModel(UsuarioResponse usuario) {
        return EntityModel.of(usuario,
            // Link a sí mismo: GET /usuario/usuarios/{id}
            linkTo(methodOn(UsuarioController.class).getUsuarioById(usuario.getId())).withSelfRel(),
            // Link a la colección completa de usuarios activos
            linkTo(methodOn(UsuarioController.class).getUsuarioActivo()).withRel("usuarios activos"),
            // Link a la colección completa
            linkTo(methodOn(UsuarioController.class).getAllUsuario()).withRel("todos los usuarios"));
    }
}
