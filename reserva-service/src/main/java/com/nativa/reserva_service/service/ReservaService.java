package com.nativa.reserva_service.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nativa.reserva_service.dto.ReservaRequest;
import com.nativa.reserva_service.dto.ReservaResponse;
import com.nativa.reserva_service.exception.BadRequestException;
import com.nativa.reserva_service.exception.ResourceNotFoundException;
import com.nativa.reserva_service.mapper.ReservaMapper;
import com.nativa.reserva_service.repository.MesaRepository;
import com.nativa.reserva_service.repository.ReservaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final ReservaMapper reservaMapper;

    @Transactional(readOnly = true)
    public List<ReservaResponse> getAllReservas() {
        return reservaRepository.findByOrderByFechaCreacionDesc()
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse getReservaById(Long id) {
        return reservaRepository.findById(id)
                .map(reservaMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
    }

    @Transactional
    public ReservaResponse createReserva(ReservaRequest request) {
        var mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + request.getMesaId()));

        if (!Boolean.TRUE.equals(mesa.getDisponible())) {
            throw new BadRequestException("La mesa no está disponible para reservas");
        }

        if (request.getCantidadPersonas() > mesa.getCapacidad()) {
            throw new BadRequestException(
                    "La cantidad de personas (" + request.getCantidadPersonas()
                            + ") excede la capacidad de la mesa (" + mesa.getCapacidad() + ")");
        }

        LocalTime inicio = request.getHora().minusHours(2);
        LocalTime fin = request.getHora().plusHours(2);

        var conflictos = reservaRepository
                .findByMesaIdAndFechaAndHoraBetween(request.getMesaId(), request.getFecha(), inicio, fin)
                .stream()
                .filter(r -> !"CANCELADA".equals(r.getEstado()))
                .toList();

        if (!conflictos.isEmpty()) {
            throw new BadRequestException(
                    "La mesa ya tiene una reserva en un rango cercano a las " + request.getHora());
        }

        var reserva = reservaMapper.toEntity(request);
        reserva.setEstado("PENDIENTE");
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setMesa(mesa);

        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponse updateReserva(Long id, ReservaRequest request) {
        var reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        var mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + request.getMesaId()));

        reserva.setNombreCliente(request.getNombreCliente());
        reserva.setTelefono(request.getTelefono());
        reserva.setEmail(request.getEmail());
        reserva.setFecha(request.getFecha());
        reserva.setHora(request.getHora());
        reserva.setCantidadPersonas(request.getCantidadPersonas());
        reserva.setMesa(mesa);

        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    @Transactional
    public void cancelReserva(Long id) {
        var reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        reserva.setEstado("CANCELADA");
        reservaRepository.save(reserva);
    }
}
