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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reserva/mesas")
@RequiredArgsConstructor
@Slf4j
public class MesaController {

    private final MesaService mesaService;
    private final MesaModelAssembler assembler;



    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<MesaResponse>>> getAllMesas() {
        log.info("Petición HTTP GET recibida en /reserva/mesas/all - Listando todas las mesas");

        List<EntityModel<MesaResponse>> mesa = mesaService.getAllMesas()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} mesas exitosamente", mesa.size());
        return ResponseEntity.ok(CollectionModel.of(mesa,
                linkTo(methodOn(MesaController.class).getAllMesas()).withSelfRel()));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MesaResponse>>> getMesasDisponibles() {
        log.info("Petición HTTP GET recibida en /reserva/mesas - Listando mesas disponibles");

        List<EntityModel<MesaResponse>> mesa = mesaService.getMesasDisponibles()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} mesas disponibles exitosamente", mesa.size());
        return ResponseEntity.ok(CollectionModel.of(mesa,
                linkTo(methodOn(MesaController.class).getMesasDisponibles()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MesaResponse>>getMesaById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /reserva/mesas/{} - Buscando mesa", id);

        EntityModel<MesaResponse> mesa = assembler.toModel(mesaService.getMesaById(id));

        log.info("Mesa con ID {} encontrada correctamente", id);
        return ResponseEntity.ok(mesa);
    }

    @PostMapping
    public ResponseEntity<MesaResponse> createMesa(@Valid @RequestBody MesaRequest request) {
        log.info("Petición HTTP POST recibida en /reserva/mesas - Creando mesa número {}", request.getNumero());

        MesaResponse creada = mesaService.createMesa(request);

        log.info("Mesa creada exitosamente con ID {}", creada.getId());
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponse> updateMesa(@PathVariable Long id, @Valid @RequestBody MesaRequest request) {
        log.info("Petición HTTP PUT recibida en /reserva/mesas/{} - Actualizando mesa", id);

        MesaResponse actualizada = mesaService.updateMesa(id, request);

        log.info("Mesa con ID {} actualizada exitosamente", id);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMesa(@PathVariable Long id) {
        log.info("Petición HTTP DELETE recibida en /reserva/mesas/{} - Eliminando mesa", id);

        mesaService.deleteMesa(id);

        log.info("Mesa con ID {} eliminada correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }
}
