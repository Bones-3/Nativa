package com.nativa.pedido_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.nativa.pedido_service.assemblers.PedidoModelAssembler;
import com.nativa.pedido_service.dto.PedidoRequest;
import com.nativa.pedido_service.dto.PedidoResponse;
import com.nativa.pedido_service.service.PedidoService;

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


@RestController
@RequestMapping("/pedido/pedidos")
@RequiredArgsConstructor
@Slf4j
public class PedidoController {
    private final PedidoService pedidoService;
    private final PedidoModelAssembler assembler;


    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PedidoResponse>>> getAllPedidos() {
        log.info("Petición HTTP GET recibida en /pedido/pedidos - Listando pedidos");

        List<EntityModel<PedidoResponse>> pedidos = pedidoService.getAllPedido()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} pedidos exitosamente", pedidos.size());
        return ResponseEntity.ok(CollectionModel.of(pedidos,
                linkTo(methodOn(PedidoController.class).getAllPedidos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PedidoResponse>> getPedidoById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /pedido/pedidos/{} - Buscando pedido", id);

        EntityModel<PedidoResponse> pedido = assembler.toModel(pedidoService.getPedidoById(id));

        log.info("Pedido con ID {} encontrado correctamente", id);
        return ResponseEntity.ok(pedido);
    }

    @PostMapping()
    public ResponseEntity<PedidoResponse> postCreatePedido(@RequestBody PedidoRequest request) {
        log.info("Petición HTTP POST recibida en /pedido/pedidos - Creando pedido para usuario {}", request.getUsuarioId());

        PedidoResponse creado = pedidoService.createPedidoResponse(request);

        log.info("Pedido creado exitosamente con ID {}", creado.getId());
        return ResponseEntity.ok(creado);
    }

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Void> deletePedidoById(@PathVariable Long id) {
        log.info("Petición HTTP DELETE recibida en /pedido/pedidos/eliminar/{} - Eliminando pedido", id);

        pedidoService.deletePedido(id);

        log.info("Pedido con ID {} eliminado correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }
}
