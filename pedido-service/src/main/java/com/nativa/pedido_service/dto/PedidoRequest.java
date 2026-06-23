package com.nativa.pedido_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoRequest {
    @NotNull(message = "El id del usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El tipo de entrega es obligatorio")
    @Pattern(regexp = "DELIVERY|RETIRO", message = "El tipo de entrega debe ser DELIVERY o RETIRO")
    private String tipoEntrega;
}
