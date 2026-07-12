package com.nativa.menu_service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.menu_service.assemblers.CategoriaModelAssembler;
import com.nativa.menu_service.dto.CategoriaRequest;
import com.nativa.menu_service.dto.CategoriaResponse;
import com.nativa.menu_service.service.CategoriaService;

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
@RequestMapping("/menu/categorias")
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categoria", description = "Operaciones relacionadas con las categorías del menú")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final CategoriaModelAssembler assembler;

    @Operation(summary = "Obtener categorías disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorías disponibles obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = CategoriaResponse.class)))
    })
    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<CategoriaResponse>>> getCategoriasDisponible() {
        log.info("Petición HTTP GET recibida en /menu/categorias - Listando categorías disponibles");

        List<EntityModel<CategoriaResponse>> categoria = categoriaService.getCategoriasDisponible()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} categorías disponibles exitosamente", categoria.size());
        return ResponseEntity.ok(CollectionModel.of(categoria,
                linkTo(methodOn(CategoriaController.class).getCategoriasDisponible()).withSelfRel()));
    }

    @Operation(summary = "Obtener todas las categorías")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de todas las categorías obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = CategoriaResponse.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<CategoriaResponse>>> getAllCategorias() {
        log.info("Petición HTTP GET recibida en /menu/categorias/all - Listando todas las categorías");

        List<EntityModel<CategoriaResponse>> categoria = categoriaService.getAllCategorias()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} categorías totales exitosamente", categoria.size());
        return ResponseEntity.ok(CollectionModel.of(categoria,
                linkTo(methodOn(CategoriaController.class).getAllCategorias()).withSelfRel()));
    }

    @Operation(summary = "Obtener categoría por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría encontrada",
            content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaResponse>> getCategoriaById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /menu/categorias/{} - Buscando categoría", id);

        var categoriaResponse = categoriaService.getCategoriaById(id);

        log.info("Categoría con ID {} encontrada correctamente", id);
        return ResponseEntity.ok(assembler.toModel(categoriaResponse));
    }

    @Operation(summary = "Crear categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría creada exitosamente",
            content = @Content(schema = @Schema(implementation = CategoriaResponse.class)))
    })
    @PostMapping()
    public ResponseEntity<CategoriaResponse> createCategoria(@Valid @RequestBody CategoriaRequest categoriaRequest) {
        log.info("Petición HTTP POST recibida en /menu/categorias - Creando nueva categoría con nombre: '{}'", categoriaRequest.getNombre());

        CategoriaResponse creado = categoriaService.createCategoria(categoriaRequest);

        log.info("Respuesta enviada de categoría creada exitosamente");
        return ResponseEntity.ok(creado);
    }

    @Operation(summary = "Actualizar categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> updateCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequest categoriaRequest) {
        log.info("Petición HTTP PUT recibida en /menu/categorias/{} - Actualizando datos", id);

        CategoriaResponse actualizado = categoriaService.updateCategoria(id, categoriaRequest);

        log.info("Respuesta enviada de categoría con ID {} actualizada exitosamente", id);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        log.info("Petición HTTP DELETE recibida en /menu/categorias/{} - Eliminación lógica", id);

        categoriaService.deleteCategoria(id);

        log.info("Categoría con ID {} deshabilitada. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }
}
