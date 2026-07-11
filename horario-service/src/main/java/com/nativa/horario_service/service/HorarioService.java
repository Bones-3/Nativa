package com.nativa.horario_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nativa.horario_service.dto.HorarioRequest;
import com.nativa.horario_service.dto.HorarioResponse;
import com.nativa.horario_service.exception.ResourceNotFoundException;
import com.nativa.horario_service.mapper.HorarioMapper;
import com.nativa.horario_service.repository.HorarioRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HorarioService {
    private final HorarioRepository horarioRepository;
    private final HorarioMapper horarioMapper;


    @Transactional(readOnly = true)
    public List <HorarioResponse> getAllHorarios() {
        return horarioRepository.findAll()
                .stream()
                .map(horarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public HorarioResponse getHorarioById(Long id) {
        return horarioRepository.findById(id)
                .map(horarioMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));
    }

    @Transactional
    public HorarioResponse createHorario(HorarioRequest request) {
        var horario = horarioMapper.toEntity(request);
        return horarioMapper.toResponse(horarioRepository.save(horario));
    }

    @Transactional
    public void deleteHorario(Long id) {
        log.info("Iniciando desactivación (eliminación lógica) del horario con ID: {}", id);

        var horario = horarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al eliminar: El horario con ID {} no existe", id);
                    return new ResourceNotFoundException("Horario no encontrado");
                });

        horario.setCerrado(true);
        horarioRepository.save(horario);

        log.info("Horario con ID: {} deshabilitado correctamente ('cerrado' establecido en true)", id);
    }


}
