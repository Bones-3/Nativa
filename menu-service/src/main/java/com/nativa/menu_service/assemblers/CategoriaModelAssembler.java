package com.nativa.menu_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.nativa.menu_service.controller.CategoriaController;
import com.nativa.menu_service.dto.CategoriaResponse;

@Component
public class CategoriaModelAssembler implements RepresentationModelAssembler<CategoriaResponse, EntityModel<CategoriaResponse>>{

    @Override
    public EntityModel<CategoriaResponse> toModel(CategoriaResponse categoria) {
        return EntityModel.of(categoria,
            // Link a sí mismo: GET /menu/categorias/{id}
            linkTo(methodOn(CategoriaController.class).getCategoriaById(categoria.getId())).withSelfRel(),
            // Link a la colección completa
            linkTo(methodOn(CategoriaController.class).getAllCategorias()).withRel("categorias"),
            // Link a solo los disponibles
            linkTo(methodOn(CategoriaController.class).getCategoriasDisponible()).withRel("categorias-disponibles"));
    }
}
