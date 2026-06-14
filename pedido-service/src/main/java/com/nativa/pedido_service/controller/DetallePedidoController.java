package com.nativa.pedido_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.pedido_service.dto.DetallePedidoRequest;
import com.nativa.pedido_service.dto.DetallePedidoResponse;
import com.nativa.pedido_service.service.DetallePedidoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/detallepedidos")
@RequiredArgsConstructor
public class DetallePedidoController {
    private final DetallePedidoService detallePedidoService;

    @GetMapping()
    public ResponseEntity<List<DetallePedidoResponse>> getAllDetallePedidos() {
        return ResponseEntity.ok(detallePedidoService.getAllDetallePedido());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoResponse> getAllDetallePedidoById(@PathVariable Long id) {
        return ResponseEntity.ok(detallePedidoService.getDetallePedidoById(id));
    }

    @PostMapping()
    public ResponseEntity<DetallePedidoResponse> postCreateDetallePedido(@PathVariable Long id, @RequestBody DetallePedidoRequest request) {
        return ResponseEntity.ok(detallePedidoService.createDetalleResponse(id, request));
    }
    
    @PutMapping("modificar/{id}")
    public ResponseEntity<DetallePedidoResponse> putUpdateDetallePedido(@PathVariable Long id, @RequestBody DetallePedidoRequest request) {
        return ResponseEntity.ok(detallePedidoService.updateDetallePedidoResponse(id, request));
    }

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Void> deleteDetallePedido(@PathVariable Long id) {
        detallePedidoService.deleteDetallePedido(id);
        return ResponseEntity.ok().build(); 
    }

}
