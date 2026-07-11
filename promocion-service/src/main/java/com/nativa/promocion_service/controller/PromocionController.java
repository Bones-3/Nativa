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

@RestController
@RequestMapping("/promocion/promociones")
@RequiredArgsConstructor
public class PromocionController {
    private final PromocionService promocionService;

    @GetMapping()
    public ResponseEntity<List<PromocionResponse>> getAllPromociones(){
        return ResponseEntity.ok(promocionService.getAllPromociones());
    } 

    @GetMapping("/{id}")
    public ResponseEntity<PromocionResponse> getPromocionById(@Valid@PathVariable Long id) {
        return ResponseEntity.ok(promocionService.getPromocionById(id));
    }

    @PostMapping()
    public ResponseEntity<PromocionResponse> createPromocion(@Valid@RequestBody PromocionRequest request){
        return ResponseEntity.ok(promocionService.createPromocion(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromocion(@Valid@PathVariable Long id){
        promocionService.deletePromocion(id);
        return ResponseEntity.noContent().build();
    }

}