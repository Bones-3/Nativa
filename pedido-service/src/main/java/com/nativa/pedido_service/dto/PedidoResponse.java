package com.nativa.pedido_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PedidoResponse {
    private Long id;
    private Long UsuarioId;
    private LocalDateTime fechaPedido;
    private String tipoEntrega;
    private BigDecimal totalPagar;
    private String tipoPago;

}
