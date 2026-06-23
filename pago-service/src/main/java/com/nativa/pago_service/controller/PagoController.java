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

@RestController
@RequestMapping("/pago/pagos")
@RequiredArgsConstructor
public class PagoController {
    
    private final PagoService pagoService;
    private final PagoModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PagoResponse>>> getAllPagos() {
        List<EntityModel<PagoResponse>> pago = pagoService.getAllPagos()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(pago,
                linkTo(methodOn(PagoController.class).getAllPagos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PagoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(pagoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<PagoResponse> createPago(@Valid @RequestBody PagoRequest request) {
        PagoResponse pago = pagoService.createPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }
}
