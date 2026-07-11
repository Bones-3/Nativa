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

@RestController
@Component
@RequestMapping("/horario/horarios")
@RequiredArgsConstructor
public class HorarioController {
    private final HorarioService horarioService;

    @GetMapping()
    public ResponseEntity<List<HorarioResponse>> getAllHorarios() {

        return ResponseEntity.ok(horarioService.getAllHorarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponse> getHorarioById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(horarioService.getHorarioById(id));
    }

    @PostMapping()
    public ResponseEntity<HorarioResponse> createHorario(@Valid @RequestBody HorarioRequest request){
        return ResponseEntity.ok(horarioService.createHorario(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorario(@Valid @PathVariable Long id){
        horarioService.deleteHorario(id);
        return ResponseEntity.noContent().build();
    }

}