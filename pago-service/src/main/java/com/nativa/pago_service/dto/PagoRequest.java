package com.nativa.pago_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagoRequest {
    @NotNull(message = "El pedido_id es obligatoria")
    private Long pedido_id;

    @NotNull(message = "La usuario_id es obligatoria")
    private Long usuario_id;

    @NotBlank(message = "El método de pago no puede ser nulo")
    private String metodoPago;
}
