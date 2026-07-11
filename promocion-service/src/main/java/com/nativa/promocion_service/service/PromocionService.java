package com.nativa.promocion_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nativa.promocion_service.dto.PromocionRequest;
import com.nativa.promocion_service.dto.PromocionResponse;
import com.nativa.promocion_service.exception.ResourceNotFoundException;
import com.nativa.promocion_service.mapper.PromocionMapper;
import com.nativa.promocion_service.repository.PromocionRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromocionService {
    private final PromocionRepository promocionRepository;
    private final PromocionMapper promocionMapper;

    @Transactional(readOnly = true)
    public List <PromocionResponse> getAllPromociones() {
        log.info("Solicitando la lista de todas las promociones");
        return promocionRepository.findAll()
                .stream()
                .map(promocionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PromocionResponse getPromocionById(Long id) {
        log.info("Buscando promoción con ID: {}", id);
        return promocionRepository.findById(id)
                .map(promocionMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontró la promoción con ID: {}", id);
                    return new ResourceNotFoundException("Promocion no encontrada");
                });
    }

    @Transactional
    public PromocionResponse createPromocion(PromocionRequest request) {
        log.info("Iniciando creación de promoción con código: {}", request.getCodigo());
        var promocion = promocionMapper.toEntity(request);
        var promocionGuardada = promocionRepository.save(promocion);
        log.info("Promoción creada exitosamente con ID {}", promocionGuardada.getId());
        return promocionMapper.toResponse(promocionGuardada);
    }

    @Transactional
    public void deletePromocion(Long id) {
        log.info("Eliminando promoción con ID: {}", id);
        promocionRepository.deleteById(id);
        log.info("Promoción con ID {} eliminada correctamente", id);
    }

}
