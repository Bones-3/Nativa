package com.example.inventario.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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

import com.example.inventario.assemblers.InventarioModelAssembler;
import com.example.inventario.dto.InventarioRequest;
import com.example.inventario.dto.InventarioResponse;
import com.example.inventario.service.InventarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventario", description = "Operaciones relacionadas con el inventario")
public class InventarioController {

    private final InventarioService inventarioService;
    private final InventarioModelAssembler assembler;

    @Operation(summary = "Crear inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class)))
    })
    @PostMapping
    public ResponseEntity<InventarioResponse> crear(@RequestBody InventarioRequest request) {
        log.info("Petición HTTP POST recibida en /inventario - Creando inventario para producto {}", request.getProductoId());

        InventarioResponse creado = inventarioService.crearInventario(request);

        log.info("Inventario creado exitosamente con ID {}", creado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Obtener todos los inventarios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inventarios obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<InventarioResponse>>> obtenerTodos() {
        log.info("Petición HTTP GET recibida en /inventario - Listando inventarios");

        List<EntityModel<InventarioResponse>> lista = inventarioService.obtenerTodos()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} inventarios exitosamente", lista.size());
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(InventarioController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener inventario por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventario encontrado",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<InventarioResponse>> obtenerPorId(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /inventario/{} - Buscando inventario", id);

        InventarioResponse inventario = inventarioService.obtenerPorId(id);

        log.info("Inventario con ID {} encontrado correctamente", id);
        return ResponseEntity.ok(assembler.toModel(inventario));
    }

    @Operation(summary = "Obtener inventario por producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventario encontrado",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<EntityModel<InventarioResponse>> obtenerPorProductoId(@PathVariable Long productoId) {
        log.info("Petición HTTP GET recibida en /inventario/producto/{} - Buscando inventario", productoId);

        InventarioResponse inventario = inventarioService.obtenerPorProductoId(productoId);

        log.info("Inventario del producto {} encontrado correctamente", productoId);
        return ResponseEntity.ok(assembler.toModel(inventario));
    }

    @Operation(summary = "Obtener inventarios con stock bajo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inventarios con stock bajo",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class)))
    })
    @GetMapping("/stock/bajo")
    public ResponseEntity<CollectionModel<EntityModel<InventarioResponse>>> obtenerStockBajo() {
        log.info("Petición HTTP GET recibida en /inventario/stock/bajo - Listando inventarios con stock bajo");

        List<EntityModel<InventarioResponse>> lista = inventarioService.obtenerStockBajo()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} inventarios con stock bajo", lista.size());
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(InventarioController.class).obtenerStockBajo()).withSelfRel()));
    }

    @Operation(summary = "Actualizar inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponse> actualizar(@PathVariable Long id, @RequestBody InventarioRequest request) {
        log.info("Petición HTTP PUT recibida en /inventario/{} - Actualizando inventario", id);

        InventarioResponse actualizado = inventarioService.actualizar(id, request);

        log.info("Inventario con ID {} actualizado exitosamente", id);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Inventario eliminado exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición HTTP DELETE recibida en /inventario/{} - Eliminando inventario", id);

        inventarioService.eliminar(id);

        log.info("Inventario con ID {} eliminado correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }
}
