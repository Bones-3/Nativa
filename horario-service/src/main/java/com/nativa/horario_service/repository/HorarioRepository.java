package com.nativa.horario_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nativa.horario_service.model.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Long>{
}
