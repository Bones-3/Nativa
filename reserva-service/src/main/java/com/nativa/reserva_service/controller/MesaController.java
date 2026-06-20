package com.nativa.reserva_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    public ResponseEntity<List<MesaResponse>> getMesasDisponibles() {
        return ResponseEntity.ok(mesaService.getMesasDisponibles());
    }

    @GetMapping("/all")
    public ResponseEntity<List<MesaResponse>> getAllMesas() {
        return ResponseEntity.ok(mesaService.getAllMesas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponse> getMesaById(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.getMesaById(id));
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
