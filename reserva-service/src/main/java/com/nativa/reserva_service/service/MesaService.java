package com.nativa.reserva_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nativa.reserva_service.dto.MesaRequest;
import com.nativa.reserva_service.dto.MesaResponse;
import com.nativa.reserva_service.exception.ResourceNotFoundException;
import com.nativa.reserva_service.mapper.MesaMapper;
import com.nativa.reserva_service.repository.MesaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MesaService {

    private final MesaRepository mesaRepository;
    private final MesaMapper mesaMapper;

    @Transactional(readOnly = true)
    public List<MesaResponse> getMesasDisponibles() {
        log.info("Solicitando la lista de mesas disponibles");
        return mesaRepository.findByDisponibleTrue()
                .stream()
                .map(mesaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MesaResponse> getAllMesas() {
        log.info("Solicitando la lista de todas las mesas");
        return mesaRepository.findAll()
                .stream()
                .map(mesaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MesaResponse getMesaById(Long id) {
        log.info("Buscando mesa con ID: {}", id);
        return mesaRepository.findById(id)
                .map(mesaMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontró la mesa con ID: {}", id);
                    return new ResourceNotFoundException("Mesa no encontrada con id: " + id);
                });
    }

    @Transactional
    public MesaResponse createMesa(MesaRequest request) {
        log.info("Iniciando creación de mesa número {}", request.getNumero());
        var mesa = mesaMapper.toEntity(request);
        var mesaGuardada = mesaRepository.save(mesa);
        log.info("Mesa creada exitosamente con ID {}", mesaGuardada.getId());
        return mesaMapper.toResponse(mesaGuardada);
    }

    @Transactional
    public MesaResponse updateMesa(Long id, MesaRequest request) {
        log.info("Iniciando actualización de la mesa con ID: {}", id);

        var mesa = mesaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar: Mesa con ID {} no existe", id);
                    return new ResourceNotFoundException("Mesa no encontrada con id: " + id);
                });

        mesa.setNumero(request.getNumero());
        mesa.setCapacidad(request.getCapacidad());
        mesa.setUbicacion(request.getUbicacion());

        var mesaActualizada = mesaRepository.save(mesa);
        log.info("Mesa con ID: {} actualizada correctamente", id);
        return mesaMapper.toResponse(mesaActualizada);
    }

    @Transactional
    public void deleteMesa(Long id) {
        log.info("Iniciando desactivación de la mesa con ID: {}", id);

        var mesa = mesaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al eliminar: Mesa con ID {} no existe", id);
                    return new ResourceNotFoundException("Mesa no encontrada con id: " + id);
                });

        mesa.setDisponible(false);
        mesaRepository.save(mesa);

        log.info("Mesa con ID: {} deshabilitada correctamente", id);
    }
}
