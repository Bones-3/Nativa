package com.nativa.pedido_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.nativa.pedido_service.assemblers.DetallePedidoModelAssembler;
import com.nativa.pedido_service.dto.DetallePedidoRequest;
import com.nativa.pedido_service.dto.DetallePedidoResponse;
import com.nativa.pedido_service.service.DetallePedidoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class DetallePedidoController {
    private final DetallePedidoService detallePedidoService;
    private final DetallePedidoModelAssembler assembler;

    // Si usas HATEOAS — firma y return consistentes
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<DetallePedidoResponse>>> getAllDetallePedidos() {
        log.info("Petición HTTP GET recibida en /pedido/detallepedidos - Listando detalles de pedido");

        List<EntityModel<DetallePedidoResponse>> detalles = detallePedidoService.getAllDetallePedido()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} detalles de pedido exitosamente", detalles.size());
        return ResponseEntity.ok(CollectionModel.of(detalles,
                linkTo(methodOn(DetallePedidoController.class).getAllDetallePedidos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DetallePedidoResponse>> getDetallePedidoById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /pedido/detallepedidos/{} - Buscando detalle de pedido", id);

        EntityModel<DetallePedidoResponse> detalle = assembler.toModel(detallePedidoService.getDetallePedidoById(id));

        log.info("Detalle de pedido con ID {} encontrado correctamente", id);
        return ResponseEntity.ok(detalle);
    }

    @PostMapping()
    public ResponseEntity<DetallePedidoResponse> postCreateDetallePedido(@RequestBody DetallePedidoRequest request) {
        log.info("Petición HTTP POST recibida en /pedido/detallepedidos - Creando detalle para pedido {}", request.getPedidoId());

        DetallePedidoResponse creado = detallePedidoService.createDetalleResponse(request);

        log.info("Detalle de pedido creado exitosamente con ID {}", creado.getId());
        return ResponseEntity.ok(creado);
    }

    @PutMapping("modificar/{id}")
    public ResponseEntity<DetallePedidoResponse> putUpdateDetallePedido(@PathVariable Long id, @RequestBody DetallePedidoRequest request) {
        log.info("Petición HTTP PUT recibida en /pedido/detallepedidos/modificar/{} - Actualizando detalle de pedido", id);

        DetallePedidoResponse actualizado = detallePedidoService.updateDetallePedidoResponse(id, request);

        log.info("Detalle de pedido con ID {} actualizado exitosamente", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Void> deleteDetallePedido(@PathVariable Long id) {
        log.info("Petición HTTP DELETE recibida en /pedido/detallepedidos/eliminar/{} - Eliminando detalle de pedido", id);

        detallePedidoService.deleteDetallePedido(id);

        log.info("Detalle de pedido con ID {} eliminado correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }

}
