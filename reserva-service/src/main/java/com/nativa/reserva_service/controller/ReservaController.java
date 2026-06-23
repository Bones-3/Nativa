package com.nativa.reserva_service.controller;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.reserva_service.assemblers.MesaModelAssembler;
import com.nativa.reserva_service.assemblers.ReservaModelAssembler;
import com.nativa.reserva_service.dto.MesaResponse;
import com.nativa.reserva_service.dto.ReservaRequest;
import com.nativa.reserva_service.dto.ReservaResponse;
import com.nativa.reserva_service.service.ReservaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaModelAssembler assembler;
    

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ReservaResponse>>> getAllReservas() {
        List<EntityModel<ReservaResponse>> reserva = reservaService.getAllReservas()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(reserva,
                linkTo(methodOn(ReservaController.class).getAllReservas()).withSelfRel()));
    }
                

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReservaResponse>> getReservaById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(reservaService.getReservaById(id)));
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> createReserva(@Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.ok(reservaService.createReserva(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> updateReserva(@PathVariable Long id, @Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.ok(reservaService.updateReserva(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReserva(@PathVariable Long id) {
        reservaService.cancelReserva(id);
        return ResponseEntity.noContent().build();
    }
}
