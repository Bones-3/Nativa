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

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;
    private final MesaMapper mesaMapper;

    @Transactional(readOnly = true)
    public List<MesaResponse> getMesasDisponibles() {
        return mesaRepository.findByDisponibleTrue()
                .stream()
                .map(mesaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MesaResponse> getAllMesas() {
        return mesaRepository.findAll()
                .stream()
                .map(mesaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MesaResponse getMesaById(Long id) {
        return mesaRepository.findById(id)
                .map(mesaMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));
    }

    @Transactional
    public MesaResponse createMesa(MesaRequest request) {
        var mesa = mesaMapper.toEntity(request);
        return mesaMapper.toResponse(mesaRepository.save(mesa));
    }

    @Transactional
    public MesaResponse updateMesa(Long id, MesaRequest request) {
        var mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));

        mesa.setNumero(request.getNumero());
        mesa.setCapacidad(request.getCapacidad());
        mesa.setUbicacion(request.getUbicacion());

        return mesaMapper.toResponse(mesaRepository.save(mesa));
    }

    @Transactional
    public void deleteMesa(Long id) {
        var mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));

        mesa.setDisponible(false);
        mesaRepository.save(mesa);
    }
}
