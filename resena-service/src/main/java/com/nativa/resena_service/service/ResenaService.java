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

@Service
@RequiredArgsConstructor
public class ResenaService {
    private final ResenaRepository resenaRepository;
    private final ResenaMapper resenaMapper;


    @Transactional(readOnly = true)
    public List <ResenaResponse> getAllResenas() {
        return resenaRepository.findAll()
                .stream()
                .map(resenaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResenaResponse getResenaById(Long id) {
        return resenaRepository.findById(id)
                .map(resenaMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));
    }

    @Transactional
    public ResenaResponse createResena(ResenaRequest request) {
        var resena = resenaMapper.toEntity(request);
        return resenaMapper.toResponse(resenaRepository.save(resena));
    }

    @Transactional
    public void resenaPedido(Long id) {
        resenaRepository.deleteById(id);
    }



}
