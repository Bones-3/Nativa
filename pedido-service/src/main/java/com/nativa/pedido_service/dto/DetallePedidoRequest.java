package com.nativa.pedido_service.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetallePedidoRequest {
    private Long ProductoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
