package com.nativa.promocion_service.controller;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.promocion_service.assemblers.PromocionModelAssembler;
import com.nativa.promocion_service.dto.PromocionResponse;
import com.nativa.promocion_service.service.PromocionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Component
@RequestMapping("/promocionV2/promociones")
@RequiredArgsConstructor
@Slf4j
public class PromocionControllerV2 {
    private final PromocionService promocionService;
    private final PromocionModelAssembler assembler;

    // Si usas HATEOAS — firma y return consistentes
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PromocionResponse>>> getAllPromociones() {
        log.info("Petición HTTP GET recibida en /promocionV2/promociones - Listando promociones");

        List<EntityModel<PromocionResponse>> detalles = promocionService.getAllPromociones()
                .stream()
                .map(assembler::toModel)
                .toList();

        log.info("Se retornaron {} promociones exitosamente", detalles.size());
        return ResponseEntity.ok(CollectionModel.of(detalles,
                linkTo(methodOn(PromocionControllerV2.class).getAllPromociones()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PromocionResponse>> getPromocionById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /promocionV2/promociones/{} - Buscando promoción", id);

        EntityModel<PromocionResponse> promocion = assembler.toModel(promocionService.getPromocionById(id));

        log.info("Promoción con ID {} encontrada correctamente", id);
        return ResponseEntity.ok(promocion);
    }
}
