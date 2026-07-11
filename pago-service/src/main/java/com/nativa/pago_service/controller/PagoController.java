package com.nativa.pago_service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.pago_service.assemblers.PagoModelAssembler;
import com.nativa.pago_service.dto.PagoRequest;
import com.nativa.pago_service.dto.PagoResponse;
import com.nativa.pago_service.service.PagoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/pago/pagos")
@RequiredArgsConstructor
@Slf4j
public class PagoController {

    private final PagoService pagoService;
    private final PagoModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PagoResponse>>> getAllPagos() {
        log.info("Petición HTTP GET recibida en /pago/pagos - Listando pagos");

        List<EntityModel<PagoResponse>> pago = pagoService.getAllPagos()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} pagos exitosamente", pago.size());
        return ResponseEntity.ok(CollectionModel.of(pago,
                linkTo(methodOn(PagoController.class).getAllPagos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PagoResponse>> getById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /pago/pagos/{} - Buscando pago", id);

        EntityModel<PagoResponse> pago = assembler.toModel(pagoService.findById(id));

        log.info("Pago con ID {} encontrado correctamente", id);
        return ResponseEntity.ok(pago);
    }

    @PostMapping
    public ResponseEntity<PagoResponse> createPago(@Valid @RequestBody PagoRequest request) {
        log.info("Petición HTTP POST recibida en /pago/pagos - Creando pago para pedido {}", request.getPedido_id());

        PagoResponse pago = pagoService.createPago(request);

        log.info("Pago creado exitosamente con ID {}", pago.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }
}
