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

@RestController
@Component
@RequestMapping("/resena/resenas")
@RequiredArgsConstructor
@Slf4j
public class ResenaController {
    private final ResenaService resenaService;
    private final ResenaModelAssembler assembler;

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

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ResenaResponse>> getResenaById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /resena/resenas/{} - Buscando reseña", id);

        EntityModel<ResenaResponse> resena = assembler.toModel(resenaService.getResenaById(id));

        log.info("Reseña con ID {} encontrada correctamente", id);
        return ResponseEntity.ok(resena);
    }

    @PostMapping()
    public ResponseEntity<ResenaResponse> createResena(@RequestBody ResenaRequest request){
        log.info("Petición HTTP POST recibida en /resena/resenas - Creando reseña para producto {}", request.getProductoId());

        ResenaResponse creada = resenaService.createResena(request);

        log.info("Reseña creada exitosamente con ID {}", creada.getId());
        return ResponseEntity.ok(creada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> resenaPedido(@PathVariable Long id){
        log.info("Petición HTTP DELETE recibida en /resena/resenas/{} - Eliminando reseña", id);

        resenaService.resenaPedido(id);

        log.info("Reseña con ID {} eliminada correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }

}