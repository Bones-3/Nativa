package com.nativa.promocion_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.promocion_service.dto.PromocionRequest;
import com.nativa.promocion_service.dto.PromocionResponse;
import com.nativa.promocion_service.service.PromocionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/promocion/promociones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Promocion", description = "Operaciones relacionadas con las promociones")
public class PromocionController {
    private final PromocionService promocionService;

    @Operation(summary = "Obtener todas las promociones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de promociones obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = PromocionResponse.class)))
    })
    @GetMapping()
    public ResponseEntity<List<PromocionResponse>> getAllPromociones(){
        log.info("Petición HTTP GET recibida en /promocion/promociones - Listando promociones");

        List<PromocionResponse> promociones = promocionService.getAllPromociones();

        log.info("Se retornaron {} promociones exitosamente", promociones.size());
        return ResponseEntity.ok(promociones);
    }

    @Operation(summary = "Obtener promoción por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promoción encontrada",
            content = @Content(schema = @Schema(implementation = PromocionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Promoción no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PromocionResponse> getPromocionById(@Valid@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /promocion/promociones/{} - Buscando promoción", id);

        PromocionResponse promocion = promocionService.getPromocionById(id);

        log.info("Promoción con ID {} encontrada correctamente", id);
        return ResponseEntity.ok(promocion);
    }

    @Operation(summary = "Crear promoción")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promoción creada exitosamente",
            content = @Content(schema = @Schema(implementation = PromocionResponse.class)))
    })
    @PostMapping()
    public ResponseEntity<PromocionResponse> createPromocion(@Valid@RequestBody PromocionRequest request){
        log.info("Petición HTTP POST recibida en /promocion/promociones - Creando promoción con código: {}", request.getCodigo());

        PromocionResponse creada = promocionService.createPromocion(request);

        log.info("Promoción creada exitosamente con ID {}", creada.getId());
        return ResponseEntity.ok(creada);
    }

    @Operation(summary = "Eliminar promoción")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Promoción eliminada exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Promoción no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromocion(@Valid@PathVariable Long id){
        log.info("Petición HTTP DELETE recibida en /promocion/promociones/{} - Eliminando promoción", id);

        promocionService.deletePromocion(id);

        log.info("Promoción con ID {} eliminada correctamente. Retornando status 204 (No Content)", id);
        return ResponseEntity.noContent().build();
    }

}
