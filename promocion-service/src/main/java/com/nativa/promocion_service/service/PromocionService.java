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

@Service
@RequiredArgsConstructor
public class PromocionService {
    private final PromocionRepository promocionRepository;
    private final PromocionMapper promocionMapper;

    @Transactional(readOnly = true)
    public List <PromocionResponse> getAllPromociones() {
        return promocionRepository.findAll()
                .stream()
                .map(promocionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PromocionResponse getPromocionById(Long id) {
        return promocionRepository.findById(id)
                .map(promocionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Promocion no encontrada"));
    }

    @Transactional
    public PromocionResponse createPromocion(PromocionRequest request) {
        var promocion = promocionMapper.toEntity(request);
        return promocionMapper.toResponse(promocionRepository.save(promocion));
    }

    @Transactional
    public void deletePromocion(Long id) {
        promocionRepository.deleteById(id);
    }

}
