package com.nativa.resena_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nativa.resena_service.dto.ResenaRequest;
import com.nativa.resena_service.dto.ResenaResponse;
import com.nativa.resena_service.exception.ResourceNotFoundException;
import com.nativa.resena_service.mapper.ResenaMapper;
import com.nativa.resena_service.repository.ResenaRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResenaService {
    private final ResenaRepository resenaRepository;
    private final ResenaMapper resenaMapper;


    @Transactional(readOnly = true)
    public List <ResenaResponse> getAllResenas() {
        log.info("Solicitando la lista de todas las reseñas");
        return resenaRepository.findAll()
                .stream()
                .map(resenaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResenaResponse getResenaById(Long id) {
        log.info("Buscando reseña con ID: {}", id);
        return resenaRepository.findById(id)
                .map(resenaMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontró la reseña con ID: {}", id);
                    return new ResourceNotFoundException("Reseña no encontrada");
                });
    }

    @Transactional
    public ResenaResponse createResena(ResenaRequest request) {
        log.info("Iniciando creación de reseña para producto {}", request.getProductoId());
        var resena = resenaMapper.toEntity(request);
        var resenaGuardada = resenaRepository.save(resena);
        log.info("Reseña creada exitosamente con ID {}", resenaGuardada.getId());
        return resenaMapper.toResponse(resenaGuardada);
    }

    @Transactional
    public void resenaPedido(Long id) {
        log.info("Eliminando reseña con ID: {}", id);
        resenaRepository.deleteById(id);
        log.info("Reseña con ID {} eliminada correctamente", id);
    }



}
