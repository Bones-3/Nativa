package com.nativa.pedido_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.pedido_service.dto.PedidoResponse;
import com.nativa.pedido_service.model.Pedido;

@Component  
public class PedidoMapper {
    
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
