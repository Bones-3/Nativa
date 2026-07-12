package com.nativa.promocion_service.controller;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Component
@RequestMapping("/promocion/promociones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Promocion V2", description = "Operaciones HATEOAS relacionadas con las promociones")
public class PromocionControllerV2 {
    private final PromocionService promocionService;
    private final PromocionModelAssembler assembler;

    @Operation(summary = "Obtener todas las promociones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de promociones obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = PromocionResponse.class)))
    })
    // Si usas HATEOAS — firma y return consistentes
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
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

    @Operation(summary = "Obtener promoción por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promoción encontrada",
            content = @Content(schema = @Schema(implementation = PromocionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Promoción no encontrada", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<PromocionResponse>> getPromocionById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /promocionV2/promociones/{} - Buscando promoción", id);

        EntityModel<PromocionResponse> promocion = assembler.toModel(promocionService.getPromocionById(id));

        log.info("Promoción con ID {} encontrada correctamente", id);
        return ResponseEntity.ok(promocion);
    }
}
