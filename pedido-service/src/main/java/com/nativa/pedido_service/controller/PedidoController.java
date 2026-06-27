package com.nativa.pedido_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.nativa.pedido_service.assemblers.PedidoModelAssembler;
import com.nativa.pedido_service.dto.PedidoRequest;
import com.nativa.pedido_service.dto.PedidoResponse;
import com.nativa.pedido_service.service.PedidoService;

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


@RestController
@RequestMapping("/pedido/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;
    private final PedidoModelAssembler assembler;


    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PedidoResponse>>> getAllPedidos() {
        List<EntityModel<PedidoResponse>> pedidos = pedidoService.getAllPedido()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(pedidos,
                linkTo(methodOn(PedidoController.class).getAllPedidos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PedidoResponse>> getPedidoById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(pedidoService.getPedidoById(id)));
    }

    @PostMapping()
    public ResponseEntity<PedidoResponse> postCreatePedido(@RequestBody PedidoRequest request) {
        
        return ResponseEntity.ok(pedidoService.createPedidoResponse(request));
    }

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Void> deletePedidoById(@PathVariable Long id) {
        pedidoService.deletePedido(id);;
        return ResponseEntity.noContent().build();
    }
}
