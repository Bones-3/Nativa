package com.nativa.promocion_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nativa.promocion_service.model.Promocion;

public interface PromocionRepository extends JpaRepository<Promocion, Long>{
}
