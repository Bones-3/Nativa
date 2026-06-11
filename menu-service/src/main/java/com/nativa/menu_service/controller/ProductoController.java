package com.nativa.menu_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.menu_service.dto.ProductoRequest;
import com.nativa.menu_service.dto.ProductoResponse;
import com.nativa.menu_service.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/productos")
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
public class ProductoController {
 
    private final ProductoService productoService;

    @GetMapping()
    public ResponseEntity<List<ProductoResponse>> getAllProductos() {
        return ResponseEntity.ok(productoService.getAllProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> getProductoById(@PathVariable Long id) {    
        return ResponseEntity.ok(productoService.getProductoById(id));
    }

    @PostMapping()
    public ResponseEntity<ProductoResponse> createProducto(@Valid @RequestBody ProductoRequest productoRequest) {
        return ResponseEntity.ok(productoService.createProducto(productoRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> updateProducto(@PathVariable Long id, @Valid @RequestBody  ProductoRequest productoRequest) {
        return ResponseEntity.ok(productoService.updateProducto(id, productoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) { 
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}
