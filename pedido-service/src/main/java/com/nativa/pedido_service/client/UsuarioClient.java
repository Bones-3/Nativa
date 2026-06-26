package com.nativa.pedido_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nativa.pedido_service.client.dto.UsuarioResponse;

@FeignClient(name = "usuario-service")
public interface UsuarioClient {
    @GetMapping("/usuario/usuarios/{id}")
    UsuarioResponse obtenerPorId (@PathVariable Long id);
}
