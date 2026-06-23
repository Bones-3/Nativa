package com.nativa.menu_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.nativa.menu_service.assemblers.ProductoModelAssembler;
import com.nativa.menu_service.dto.ProductoRequest;
import com.nativa.menu_service.dto.ProductoResponse;
import com.nativa.menu_service.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

@RestController
@RequestMapping("/menu/productos")
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
public class ProductoController {
 
    private final ProductoService productoService;
    private final ProductoModelAssembler assembler;

    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponse>>> getProductosDisponibles() {
        List<EntityModel<ProductoResponse>> producto = productoService.getAllDisponible()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(producto,
                linkTo(methodOn(ProductoController.class).getAllProductos()).withSelfRel()));
    }

    @GetMapping("/categoria/{nombre}")
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponse>>> getProductosByCategoria(@PathVariable String nombre) {
        List<EntityModel<ProductoResponse>> producto = productoService.agruparProductosDisponiblesPorCategoria(nombre)
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(producto,
                linkTo(methodOn(ProductoController.class).getProductosByCategoria(nombre)).withSelfRel()));
    }

    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponse>>> getAllProductos() {
        List<EntityModel<ProductoResponse>> producto = productoService.getAllProductos()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(producto,
                linkTo(methodOn(ProductoController.class).getAllProductos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponse>> getProductoById(@PathVariable Long id) {    
        return ResponseEntity.ok(assembler.toModel(productoService.getProductoById(id)));
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
