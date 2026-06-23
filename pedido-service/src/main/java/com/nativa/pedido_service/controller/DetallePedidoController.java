package com.nativa.pedido_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.nativa.pedido_service.assemblers.DetallePedidoModelAssembler;
import com.nativa.pedido_service.dto.DetallePedidoRequest;
import com.nativa.pedido_service.dto.DetallePedidoResponse;
import com.nativa.pedido_service.service.DetallePedidoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/pedido/detallepedidos")
@RequiredArgsConstructor
public class DetallePedidoController {
    private final DetallePedidoService detallePedidoService;
    private final DetallePedidoModelAssembler assembler;

    // Si usas HATEOAS — firma y return consistentes
    public ResponseEntity<CollectionModel<EntityModel<DetallePedidoResponse>>> getAllDetallePedidos() {
        List<EntityModel<DetallePedidoResponse>> detalles = detallePedidoService.getAllDetallePedido()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(detalles,
                linkTo(methodOn(DetallePedidoController.class).getAllDetallePedidos()).withSelfRel()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DetallePedidoResponse>> getDetallePedidoById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(detallePedidoService.getDetallePedidoById(id)));
    }

    @GetMapping("/pedido/{id}")
    public ResponseEntity<CollectionModel<EntityModel<DetallePedidoResponse>>> getAllDetallePedidoByPedidoId(@PathVariable Long id) {
        List<EntityModel<DetallePedidoResponse>> detalles = detallePedidoService.getDetallePedidoByPedidoId(id)
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(detalles,
                linkTo(methodOn(DetallePedidoController.class).getAllDetallePedidoByPedidoId(id)).withSelfRel()));
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
