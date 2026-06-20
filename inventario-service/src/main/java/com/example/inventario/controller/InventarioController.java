package com.example.inventario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventario.dto.InventarioRequest;
import com.example.inventario.dto.InventarioResponse;
import com.example.inventario.service.InventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<InventarioResponse> crear(@RequestBody InventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crearInventario(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioResponse> obtenerPorProductoId(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.obtenerPorProductoId(productoId));
    }

    @GetMapping
    public ResponseEntity<List<InventarioResponse>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponse> actualizar(@PathVariable Long id, @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock/bajo")
    public ResponseEntity<List<InventarioResponse>> obtenerStockBajo() {
        return ResponseEntity.ok(inventarioService.obtenerStockBajo());
    }
}
