package com.nativa.pedido_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.nativa.pedido_service.controller.PedidoController;
import com.nativa.pedido_service.dto.PedidoResponse;

@Component
public class PedidoModelAssembler implements RepresentationModelAssembler<PedidoResponse, EntityModel<PedidoResponse>>{
    @Override
    public EntityModel<PedidoResponse> toModel(PedidoResponse pedido) {
        return EntityModel.of(pedido,
            // Link a sí mismo: GET /pedido/pedidos/{id}
            linkTo(methodOn(PedidoController.class).getPedidoById(pedido.getId())).withSelfRel(),
            // Link a la colección completa
            linkTo(methodOn(PedidoController.class).getAllPedidos()).withRel("detallesPedido"));
    }
}
