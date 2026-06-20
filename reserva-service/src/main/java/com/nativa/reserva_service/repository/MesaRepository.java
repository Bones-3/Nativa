package com.nativa.reserva_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nativa.reserva_service.model.Mesa;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findByDisponibleTrue();
}
