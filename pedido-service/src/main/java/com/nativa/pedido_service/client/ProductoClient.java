package com.nativa.pedido_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nativa.pedido_service.client.dto.ProductoResponse;

@FeignClient(name = "menu-service")
public interface ProductoClient {
    @GetMapping("/menu/productos/{id}")
    ProductoResponse obtenerProductoPorId(@PathVariable Long id);
}
