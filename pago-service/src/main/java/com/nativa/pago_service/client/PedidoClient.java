package com.nativa.pago_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nativa.pago_service.client.dto.PedidoResponse;

@FeignClient(name = "pedido-service")
public interface PedidoClient {

    @GetMapping("/pedido/pedidos/{id}")
    PedidoResponse obtenerPorId (@PathVariable Long id);

}
