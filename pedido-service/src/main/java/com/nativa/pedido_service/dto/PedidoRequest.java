package com.nativa.pedido_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoRequest {
    private Long usuarioId;
    private String tipoEntrega;
}
