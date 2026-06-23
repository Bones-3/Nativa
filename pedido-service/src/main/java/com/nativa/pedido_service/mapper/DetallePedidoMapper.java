package com.nativa.pedido_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.pedido_service.dto.DetallePedidoRequest;
import com.nativa.pedido_service.dto.DetallePedidoResponse;
import com.nativa.pedido_service.model.DetallePedido;

@Component  
public class DetallePedidoMapper {
    public DetallePedido toEntity(DetallePedidoRequest request) {

        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setProductoId(request.getProductoId());
        detallePedido.setCantidad(request.getCantidad());

        return detallePedido;   
    }

    public DetallePedidoResponse toResponse(DetallePedido detallePedido) {

        return DetallePedidoResponse.builder()
                .id(detallePedido.getId())
                .productoId(detallePedido.getProductoId())
                .pedidoId(detallePedido.getPedido().getId())
                .cantidad(detallePedido.getCantidad())
                .precioUnitario(detallePedido.getPrecioUnitario())
                .subtotal(detallePedido.getSubtotal())
                .build();
    }
}

