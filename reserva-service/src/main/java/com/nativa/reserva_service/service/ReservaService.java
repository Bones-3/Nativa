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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final ReservaMapper reservaMapper;

    @Transactional(readOnly = true)
    public List<ReservaResponse> getAllReservas() {
        log.info("Solicitando la lista de todas las reservas");
        return reservaRepository.findByOrderByFechaCreacionDesc()
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse getReservaById(Long id) {
        log.info("Buscando reserva con ID: {}", id);
        return reservaRepository.findById(id)
                .map(reservaMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontró la reserva con ID: {}", id);
                    return new ResourceNotFoundException("Reserva no encontrada con id: " + id);
                });
    }

    @Transactional
    public ReservaResponse createReserva(ReservaRequest request) {
        log.info("Iniciando creación de reserva para mesa {}", request.getMesaId());

        var mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> {
                    log.error("Error al crear reserva: mesa {} no existe", request.getMesaId());
                    return new ResourceNotFoundException("Mesa no encontrada con id: " + request.getMesaId());
                });

        if (!Boolean.TRUE.equals(mesa.getDisponible())) {
            log.warn("Reserva rechazada: mesa {} no disponible", request.getMesaId());
            throw new BadRequestException("La mesa no está disponible para reservas");
        }

        if (request.getCantidadPersonas() > mesa.getCapacidad()) {
            log.warn("Reserva rechazada: cantidad de personas ({}) excede la capacidad de la mesa {} ({})",
                    request.getCantidadPersonas(), request.getMesaId(), mesa.getCapacidad());
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
            log.warn("Reserva rechazada: mesa {} ya tiene una reserva cercana a las {}", request.getMesaId(), request.getHora());
            throw new BadRequestException(
                    "La mesa ya tiene una reserva en un rango cercano a las " + request.getHora());
        }

        var reserva = reservaMapper.toEntity(request);
        reserva.setEstado("PENDIENTE");
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setMesa(mesa);

        var reservaGuardada = reservaRepository.save(reserva);
        log.info("Reserva creada exitosamente con ID {}", reservaGuardada.getId());
        return reservaMapper.toResponse(reservaGuardada);
    }

    @Transactional
    public ReservaResponse updateReserva(Long id, ReservaRequest request) {
        log.info("Iniciando actualización de la reserva con ID: {}", id);

        var reserva = reservaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar: Reserva con ID {} no existe", id);
                    return new ResourceNotFoundException("Reserva no encontrada con id: " + id);
                });

        var mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> {
                    log.error("Error al actualizar reserva {}: mesa {} no existe", id, request.getMesaId());
                    return new ResourceNotFoundException("Mesa no encontrada con id: " + request.getMesaId());
                });

        reserva.setNombreCliente(request.getNombreCliente());
        reserva.setTelefono(request.getTelefono());
        reserva.setEmail(request.getEmail());
        reserva.setFecha(request.getFecha());
        reserva.setHora(request.getHora());
        reserva.setCantidadPersonas(request.getCantidadPersonas());
        reserva.setMesa(mesa);

        var reservaActualizada = reservaRepository.save(reserva);
        log.info("Reserva con ID: {} actualizada correctamente", id);
        return reservaMapper.toResponse(reservaActualizada);
    }

    @Transactional
    public void cancelReserva(Long id) {
        log.info("Iniciando cancelación de la reserva con ID: {}", id);

        var reserva = reservaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al cancelar: Reserva con ID {} no existe", id);
                    return new ResourceNotFoundException("Reserva no encontrada con id: " + id);
                });

        reserva.setEstado("CANCELADA");
        reservaRepository.save(reserva);

        log.info("Reserva con ID: {} cancelada correctamente", id);
    }
}
