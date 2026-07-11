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

@RestController
@Component
@RequestMapping("/horario/horarios")
@RequiredArgsConstructor
@Slf4j
public class HorarioController {
    private final HorarioService horarioService;

    @GetMapping()
    public ResponseEntity<List<HorarioResponse>> getAllHorarios() {
        log.info("Petición HTTP GET recibida en /horario/horarios - Listando horarios");

        List<HorarioResponse> horarios = horarioService.getAllHorarios();

        log.info("Se retornaron {} horarios exitosamente", horarios.size());
        return ResponseEntity.ok(horarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponse> getHorarioById(@Valid @PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /horario/horarios/{} - Buscando horario", id);

        HorarioResponse horario = horarioService.getHorarioById(id);

        log.info("Horario con ID {} encontrado correctamente", id);
        return ResponseEntity.ok(horario);
    }

    @PostMapping()
    public ResponseEntity<HorarioResponse> createHorario(@Valid @RequestBody HorarioRequest request){
        log.info("Petición HTTP POST recibida en /horario/horarios - Creando horario para día: {}", request.getDiaSemana());

        HorarioResponse creado = horarioService.createHorario(request);

        log.info("Horario creado exitosamente con ID {}", creado.getId());
        return ResponseEntity.ok(creado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorario(@Valid @PathVariable Long id){
        log.info("Petición HTTP DELETE recibida en /horario/horarios/{} - Eliminando horario", id);

        horarioService.deleteHorario(id);

        log.info("Horario con ID {} eliminado correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }

}