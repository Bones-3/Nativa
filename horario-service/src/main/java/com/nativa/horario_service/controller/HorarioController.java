package com.nativa.horario_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.horario_service.dto.HorarioRequest;
import com.nativa.horario_service.dto.HorarioResponse;
import com.nativa.horario_service.service.HorarioService;

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
@Component
@RequestMapping("/horario/horarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Horario", description = "Operaciones relacionadas con los horarios")
public class HorarioController {
    private final HorarioService horarioService;

    @Operation(summary = "Obtener todos los horarios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de horarios obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = HorarioResponse.class)))
    })
    @GetMapping()
    public ResponseEntity<List<HorarioResponse>> getAllHorarios() {
        log.info("Petición HTTP GET recibida en /horario/horarios - Listando horarios");

        List<HorarioResponse> horarios = horarioService.getAllHorarios();

        log.info("Se retornaron {} horarios exitosamente", horarios.size());
        return ResponseEntity.ok(horarios);
    }

    @Operation(summary = "Obtener horario por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horario encontrado",
            content = @Content(schema = @Schema(implementation = HorarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Horario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponse> getHorarioById(@Valid @PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /horario/horarios/{} - Buscando horario", id);

        HorarioResponse horario = horarioService.getHorarioById(id);

        log.info("Horario con ID {} encontrado correctamente", id);
        return ResponseEntity.ok(horario);
    }

    @Operation(summary = "Crear horario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horario creado exitosamente",
            content = @Content(schema = @Schema(implementation = HorarioResponse.class)))
    })
    @PostMapping()
    public ResponseEntity<HorarioResponse> createHorario(@Valid @RequestBody HorarioRequest request){
        log.info("Petición HTTP POST recibida en /horario/horarios - Creando horario para día: {}", request.getDiaSemana());

        HorarioResponse creado = horarioService.createHorario(request);

        log.info("Horario creado exitosamente con ID {}", creado.getId());
        return ResponseEntity.ok(creado);
    }

    @Operation(summary = "Eliminar horario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Horario eliminado exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Horario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorario(@Valid @PathVariable Long id){
        log.info("Petición HTTP DELETE recibida en /horario/horarios/{} - Eliminando horario", id);

        horarioService.deleteHorario(id);

        log.info("Horario con ID {} eliminado correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }

}
