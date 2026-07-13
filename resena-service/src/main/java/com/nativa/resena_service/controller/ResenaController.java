package com.nativa.resena_service.controller;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.resena_service.assemblers.ResenaModelAssembler;
import com.nativa.resena_service.dto.ResenaRequest;
import com.nativa.resena_service.dto.ResenaResponse;
import com.nativa.resena_service.service.ResenaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Component
@RequestMapping("/resena/resenas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reseñas", description = "Gestión de comentarios y calificaciones de productos")
public class ResenaController {
    private final ResenaService resenaService;
    private final ResenaModelAssembler assembler;

    @Operation(summary = "Listar reseñas", description = "Retorna todas las reseñas registradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente")
    })
    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<ResenaResponse>>> getAllResenas() {
        log.info("Petición HTTP GET recibida en /resena/resenas - Listando reseñas");

        List<EntityModel<ResenaResponse>> resenas = resenaService.getAllResenas()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} reseñas exitosamente", resenas.size());
        return ResponseEntity.ok(CollectionModel.of(resenas,
                linkTo(methodOn(ResenaController.class).getAllResenas()).withSelfRel()));
    }

    @Operation(summary = "Buscar reseña por ID", description = "Retorna una reseña según su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseña encontrada"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada"),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ResenaResponse>> getResenaById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /resena/resenas/{} - Buscando reseña", id);

        EntityModel<ResenaResponse> resena = assembler.toModel(resenaService.getResenaById(id));

        log.info("Reseña con ID {} encontrada correctamente", id);
        return ResponseEntity.ok(resena);
    }

    @Operation(summary = "Crear reseña", description = "Registra una nueva reseña de un producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseña creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en el request"),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente")
    })
    @PostMapping()
    public ResponseEntity<ResenaResponse> createResena(@Valid @RequestBody ResenaRequest request){
        log.info("Petición HTTP POST recibida en /resena/resenas - Creando reseña para producto {}", request.getProductoId());

        ResenaResponse creada = resenaService.createResena(request);

        log.info("Reseña creada exitosamente con ID {}", creada.getId());
        return ResponseEntity.ok(creada);
    }

    @Operation(summary = "Eliminar reseña", description = "Elimina una reseña por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reseña eliminada exitosamente"),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> resenaPedido(@PathVariable Long id){
        log.info("Petición HTTP DELETE recibida en /resena/resenas/{} - Eliminando reseña", id);

        resenaService.resenaPedido(id);

        log.info("Reseña con ID {} eliminada correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }

}
