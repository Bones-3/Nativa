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
import lombok.extern.slf4j.Slf4j;



@RestController
@Component
@RequestMapping("/horarioV2/horarios")
@RequiredArgsConstructor
@Slf4j
public class HorarioControllerV2 {
    private final HorarioService horarioService;
    private final HorarioModelAssembler assembler;

    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<HorarioResponse>>> getAllHorarios() {
        log.info("Petición HTTP GET recibida en /horarioV2/horarios - Listando horarios");

        List<EntityModel<HorarioResponse>> horarios = horarioService.getAllHorarios()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} horarios exitosamente", horarios.size());
        return ResponseEntity.ok(CollectionModel.of(horarios,
                linkTo(methodOn(HorarioControllerV2.class).getAllHorarios()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<HorarioResponse>> getHorarioById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /horarioV2/horarios/{} - Buscando horario", id);

        EntityModel<HorarioResponse> horario = assembler.toModel(horarioService.getHorarioById(id));

        log.info("Horario con ID {} encontrado correctamente", id);
        return ResponseEntity.ok(horario);
    }

}
