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

@RestController
@Component
@RequestMapping("/resena/resenas")
@RequiredArgsConstructor
public class ResenaController {
    private final ResenaService resenaService;
    private final ResenaModelAssembler assembler;

    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<ResenaResponse>>> getAllResenas() {
        List<EntityModel<ResenaResponse>> resenas = resenaService.getAllResenas()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(resenas,
                linkTo(methodOn(ResenaController.class).getAllResenas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ResenaResponse>> getResenaById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(resenaService.getResenaById(id)));
    }

    @PostMapping()
    public ResponseEntity<ResenaResponse> createResena(@RequestBody ResenaRequest request){
        return ResponseEntity.ok(resenaService.createResena(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> resenaPedido(@PathVariable Long id){
        resenaService.resenaPedido(id);
        return ResponseEntity.noContent().build();
    }

}