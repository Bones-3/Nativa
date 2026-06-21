package com.nativa.pedido_service.client.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductoResponse {
    private Long id;
    private BigDecimal precio;
    private Boolean disponible;
}
