package com.nativa.pago_service.client.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PedidoResponse {
    private Long id;
    private BigDecimal totalPagar;
}
