package com.nativa.pedido_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.pedido_service.dto.PedidoRequest;
import com.nativa.pedido_service.dto.PedidoResponse;
import com.nativa.pedido_service.model.Pedido;

@Component  
public class PedidoMapper {

    public Pedido toEntity(PedidoRequest request) {

        Pedido pedido = new Pedido();

        pedido.setUsuarioId(request.getUsuarioId());
        pedido.setTipoEntrega(request.getTipoEntrega());

        return pedido;   
    }
    
    public PedidoResponse toResponse(Pedido pedido) {
        return PedidoResponse.builder()
                .id(pedido.getId())
                .usuarioId(pedido.getUsuarioId())
                .fechaPedido(pedido.getFechaPedido())
                .tipoEntrega(pedido.getTipoEntrega())
                .totalPagar(pedido.getTotalPagar())
                .build();
    }
}
