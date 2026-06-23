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
@RequestMapping("/menu/categorias")
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final CategoriaModelAssembler assembler;

    public ResponseEntity<CollectionModel<EntityModel<CategoriaResponse>>> getCategoriasDisponible() {
        List<EntityModel<CategoriaResponse>> categoria = categoriaService.getCategoriasDisponible()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(categoria,
                linkTo(methodOn(CategoriaController.class).getCategoriasDisponible()).withSelfRel()));
    }

    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<CategoriaResponse>>> getAllCategorias() {
        List<EntityModel<CategoriaResponse>> categoria = categoriaService.getAllCategorias()
                .stream()
                .map(assembler::toModel)
                .toList();
        
        return ResponseEntity.ok(CollectionModel.of(categoria,
                linkTo(methodOn(CategoriaController.class).getAllCategorias()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaResponse>> getCategoriaById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(categoriaService.getCategoriaById(id)));
    }

    @PostMapping()
    public ResponseEntity<CategoriaResponse> createCategoria(@Valid @RequestBody CategoriaRequest categoriaRequest) {
        return ResponseEntity.ok(categoriaService.createCategoria(categoriaRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> updateCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequest categoriaRequest) {
        return ResponseEntity.ok(categoriaService.updateCategoria(id, categoriaRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        categoriaService.deleteCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
