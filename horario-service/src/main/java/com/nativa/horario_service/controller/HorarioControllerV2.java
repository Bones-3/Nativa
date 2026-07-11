package com.nativa.horario_service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.horario_service.assemblers.HorarioModelAssembler;
import com.nativa.horario_service.dto.HorarioResponse;
import com.nativa.horario_service.service.HorarioService;

import lombok.RequiredArgsConstructor;



@RestController
@Component
@RequestMapping("/horario/horarios")
@RequiredArgsConstructor
public class HorarioControllerV2 {
    private final HorarioService horarioService;
    private final HorarioModelAssembler assembler;

    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<HorarioResponse>>> getAllHorarios() {
        List<EntityModel<HorarioResponse>> horarios = horarioService.getAllHorarios()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(horarios,
                linkTo(methodOn(HorarioControllerV2.class).getAllHorarios()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<HorarioResponse>> getHorarioById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(horarioService.getHorarioById(id)));
    }

}
