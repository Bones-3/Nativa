package com.nativa.reserva_service.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nativa.reserva_service.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByMesaIdAndFechaAndHoraBetween(Long mesaId, LocalDate fecha, LocalTime inicio, LocalTime fin);

    List<Reserva> findByFechaOrderByHoraAsc(LocalDate fecha);

    List<Reserva> findByOrderByFechaCreacionDesc();
}
