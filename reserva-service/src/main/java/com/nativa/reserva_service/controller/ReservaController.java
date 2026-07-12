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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/reserva/reservas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reserva", description = "Operaciones relacionadas con las reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaModelAssembler assembler;


    @Operation(summary = "Obtener todas las reservas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ReservaResponse.class)))
    })
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


    @Operation(summary = "Obtener reserva por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva encontrada",
            content = @Content(schema = @Schema(implementation = ReservaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReservaResponse>> getReservaById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /reserva/reservas/{} - Buscando reserva", id);

        EntityModel<ReservaResponse> reserva = assembler.toModel(reservaService.getReservaById(id));

        log.info("Reserva con ID {} encontrada correctamente", id);
        return ResponseEntity.ok(reserva);
    }

    @Operation(summary = "Crear reserva")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva creada exitosamente",
            content = @Content(schema = @Schema(implementation = ReservaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Mesa no disponible o datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ReservaResponse> createReserva(@Valid @RequestBody ReservaRequest request) {
        log.info("Petición HTTP POST recibida en /reserva/reservas - Creando reserva para mesa {}", request.getMesaId());

        ReservaResponse creada = reservaService.createReserva(request);

        log.info("Reserva creada exitosamente con ID {}", creada.getId());
        return ResponseEntity.ok(creada);
    }

    @Operation(summary = "Actualizar reserva")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = ReservaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Reserva o mesa no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> updateReserva(@PathVariable Long id, @Valid @RequestBody ReservaRequest request) {
        log.info("Petición HTTP PUT recibida en /reserva/reservas/{} - Actualizando reserva", id);

        ReservaResponse actualizada = reservaService.updateReserva(id, request);

        log.info("Reserva con ID {} actualizada exitosamente", id);
        return ResponseEntity.ok(actualizada);
    }

    @Operation(summary = "Cancelar reserva")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reserva cancelada exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReserva(@PathVariable Long id) {
        log.info("Petición HTTP DELETE recibida en /reserva/reservas/{} - Cancelando reserva", id);

        reservaService.cancelReserva(id);

        log.info("Reserva con ID {} cancelada correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }
}
