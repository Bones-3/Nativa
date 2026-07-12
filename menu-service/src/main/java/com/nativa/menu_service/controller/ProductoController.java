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
import lombok.extern.slf4j.Slf4j;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/menu/productos")
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Producto", description = "Operaciones relacionadas con los productos del menú")
public class ProductoController {

    private final ProductoService productoService;
    private final ProductoModelAssembler assembler;

    @Operation(summary = "Obtener productos disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos disponibles obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class)))
    })
    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponse>>> getProductosDisponibles() {
        log.info("Petición HTTP GET recibida en /menu/productos - Listando productos disponibles");

        List<EntityModel<ProductoResponse>> producto = productoService.getAllDisponible()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} productos disponibles exitosamente", producto.size());
        return ResponseEntity.ok(CollectionModel.of(producto,
                linkTo(methodOn(ProductoController.class).getAllProductos()).withSelfRel()));
    }

    @Operation(summary = "Obtener productos disponibles por categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos filtrada por categoría obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class)))
    })
    @GetMapping("/categoria/{nombre}")
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponse>>> getProductosByCategoria(@PathVariable String nombre) {
        log.info("Petición HTTP GET recibida en /menu/productos/categoria/{} - Filtrando por categoría", nombre);

        List<EntityModel<ProductoResponse>> producto = productoService.agruparProductosDisponiblesPorCategoria(nombre)
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} productos para la categoría '{}'", producto.size(), nombre);
        return ResponseEntity.ok(CollectionModel.of(producto,
                linkTo(methodOn(ProductoController.class).getProductosByCategoria(nombre)).withSelfRel()));
    }

    @Operation(summary = "Obtener todos los productos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de todos los productos obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponse>>> getAllProductos() {
        log.info("Petición HTTP GET recibida en /menu/productos/all - Listando la totalidad de productos");

        List<EntityModel<ProductoResponse>> producto = productoService.getAllProductos()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} productos totales exitosamente", producto.size());
        return ResponseEntity.ok(CollectionModel.of(producto,
                linkTo(methodOn(ProductoController.class).getAllProductos()).withSelfRel()));
    }

    @Operation(summary = "Obtener producto por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponse>> getProductoById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /menu/productos/{} - Buscando producto", id);

        var productoResponse = productoService.getProductoById(id);

        log.info("Producto con ID {} encontrado correctamente", id);
        return ResponseEntity.ok(assembler.toModel(productoResponse));
    }

    @Operation(summary = "Crear producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto creado exitosamente",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @PostMapping()
    public ResponseEntity<ProductoResponse> createProducto(@Valid @RequestBody ProductoRequest productoRequest) {
        log.info("Petición HTTP POST recibida en /menu/productos - Datos recibidos para nombre: '{}'", productoRequest.getNombre());

        ProductoResponse creado = productoService.createProducto(productoRequest);

        log.info("Respuesta enviada de producto creado exitosamente");
        return ResponseEntity.ok(creado);
    }

    @Operation(summary = "Actualizar producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Producto o categoría no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> updateProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequest productoRequest) {
        log.info("Petición HTTP PUT recibida en /menu/productos/{} - Actualizando datos", id);

        ProductoResponse actualizado = productoService.updateProducto(id, productoRequest);

        log.info("Respuesta enviada de producto con ID {} actualizado exitosamente", id);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        log.info("Petición HTTP DELETE recibida en /menu/productos/{} - Eliminación lógica", id);

        productoService.deleteProducto(id);

        log.info("Producto con ID {} deshabilitado correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }
}
