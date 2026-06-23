package com.nativa.pedido_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.nativa.pedido_service.controller.DetallePedidoController;
import com.nativa.pedido_service.dto.DetallePedidoResponse;

@Component
public class DetallePedidoModelAssembler implements RepresentationModelAssembler<DetallePedidoResponse, EntityModel<DetallePedidoResponse>>{
    
    @Override
    public EntityModel<DetallePedidoResponse> toModel(DetallePedidoResponse detallePedido) {
        return EntityModel.of(detallePedido,
            // Link a sí mismo: GET /pedido/detallepedidos"{id}
            linkTo(methodOn(DetallePedidoController.class).getDetallePedidoById(detallePedido.getId())).withSelfRel(),
            // Link a la colección completa
            linkTo(methodOn(DetallePedidoController.class).getAllDetallePedidos()).withRel("detallesPedido"),
            // Link a solo la lista de detalles de un pedido
            linkTo(methodOn(DetallePedidoController.class).getAllDetallePedidoByPedidoId(detallePedido.getPedidoId())).withRel("detalles por pedido"));
    }
}
