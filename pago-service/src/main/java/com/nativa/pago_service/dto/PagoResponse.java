package com.nativa.pago_service.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PagoResponse {
    
    private Long id;
    private Long pedido_id;
    private Long usuario_id;
    private Double total;
    private String metodoPago;
    private LocalDateTime fechaPago;  
    
}
