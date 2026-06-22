package com.nativa.menu_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.nativa.menu_service.controller.ProductoController;
import com.nativa.menu_service.dto.ProductoResponse;

@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<ProductoResponse, EntityModel<ProductoResponse>> {

    @Override
    public EntityModel<ProductoResponse> toModel(ProductoResponse producto) {
        return EntityModel.of(producto,
            // Link a sí mismo: GET /menu/productos/{id}
            linkTo(methodOn(ProductoController.class).getProductoById(producto.getId())).withSelfRel(),
            // Link a la colección completa
            linkTo(methodOn(ProductoController.class).getAllProductos()).withRel("productos"),
            // Link a solo los disponibles
            linkTo(methodOn(ProductoController.class).getProductosDisponibles()).withRel("productos-disponibles"));
    }
}
