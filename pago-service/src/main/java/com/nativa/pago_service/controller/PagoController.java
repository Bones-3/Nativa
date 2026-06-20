package com.nativa.pago_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.pago_service.dto.PagoRequest;
import com.nativa.pago_service.dto.PagoResponse;
import com.nativa.pago_service.service.PagoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/pagos")
@RequiredArgsConstructor
public class PagoController {
    
    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<PagoResponse>> getAllPagos() {
        return ResponseEntity.ok(pagoService.getAllPagos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PagoResponse> createPago(@Valid @RequestBody PagoRequest request) {
        PagoResponse pago = pagoService.createPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }
}
