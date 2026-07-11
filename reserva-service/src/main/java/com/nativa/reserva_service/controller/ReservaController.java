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

import com.nativa.reserva_service.assemblers.ReservaModelAssembler;
import com.nativa.reserva_service.dto.ReservaRequest;
import com.nativa.reserva_service.dto.ReservaResponse;
import com.nativa.reserva_service.service.ReservaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reserva/reservas")
@RequiredArgsConstructor
@Slf4j
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaModelAssembler assembler;


    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ReservaResponse>>> getAllReservas() {
        log.info("Petición HTTP GET recibida en /reserva/reservas - Listando reservas");

        List<EntityModel<ReservaResponse>> reserva = reservaService.getAllReservas()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} reservas exitosamente", reserva.size());
        return ResponseEntity.ok(CollectionModel.of(reserva,
                linkTo(methodOn(ReservaController.class).getAllReservas()).withSelfRel()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReservaResponse>> getReservaById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /reserva/reservas/{} - Buscando reserva", id);

        EntityModel<ReservaResponse> reserva = assembler.toModel(reservaService.getReservaById(id));

        log.info("Reserva con ID {} encontrada correctamente", id);
        return ResponseEntity.ok(reserva);
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> createReserva(@Valid @RequestBody ReservaRequest request) {
        log.info("Petición HTTP POST recibida en /reserva/reservas - Creando reserva para mesa {}", request.getMesaId());

        ReservaResponse creada = reservaService.createReserva(request);

        log.info("Reserva creada exitosamente con ID {}", creada.getId());
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> updateReserva(@PathVariable Long id, @Valid @RequestBody ReservaRequest request) {
        log.info("Petición HTTP PUT recibida en /reserva/reservas/{} - Actualizando reserva", id);

        ReservaResponse actualizada = reservaService.updateReserva(id, request);

        log.info("Reserva con ID {} actualizada exitosamente", id);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReserva(@PathVariable Long id) {
        log.info("Petición HTTP DELETE recibida en /reserva/reservas/{} - Cancelando reserva", id);

        reservaService.cancelReserva(id);

        log.info("Reserva con ID {} cancelada correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }
}
