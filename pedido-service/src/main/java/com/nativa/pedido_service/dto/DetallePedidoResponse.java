package com.nativa.pedido_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DetallePedidoResponse {
    private Long id;
    private Long productoId;
    private Integer cantidad;
    private String precioUnitario;
    private String subtotal;
}
