package com.nativa.pedido_service.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoRequest {
    private LocalDateTime fechaPedido;
    private String tipoEntrega;
}
