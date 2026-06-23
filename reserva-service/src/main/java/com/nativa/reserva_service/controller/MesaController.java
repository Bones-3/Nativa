package com.nativa.reserva_service.controller;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

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
import com.nativa.reserva_service.dto.MesaRequest;
import com.nativa.reserva_service.dto.MesaResponse;
import com.nativa.reserva_service.service.MesaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;
    private final MesaModelAssembler assembler;



    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<MesaResponse>>> getAllMesas() {
        List<EntityModel<MesaResponse>> mesa = mesaService.getAllMesas()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(mesa,
                linkTo(methodOn(MesaController.class).getAllMesas()).withSelfRel()));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MesaResponse>>> getMesasDisponibles() {
        List<EntityModel<MesaResponse>> mesa = mesaService.getMesasDisponibles()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(mesa,
                linkTo(methodOn(MesaController.class).getMesasDisponibles()).withSelfRel()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MesaResponse>>getMesaById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(mesaService.getMesaById(id)));
    }

    @PostMapping
    public ResponseEntity<MesaResponse> createMesa(@Valid @RequestBody MesaRequest request) {
        return ResponseEntity.ok(mesaService.createMesa(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponse> updateMesa(@PathVariable Long id, @Valid @RequestBody MesaRequest request) {
        return ResponseEntity.ok(mesaService.updateMesa(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMesa(@PathVariable Long id) {
        mesaService.deleteMesa(id);
        return ResponseEntity.noContent().build();
    }
}
