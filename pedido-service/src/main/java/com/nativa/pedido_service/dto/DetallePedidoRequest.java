package com.nativa.pedido_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetallePedidoRequest {
    private Long pedidoId;
    private Long productoId;
    private Integer cantidad;
}
