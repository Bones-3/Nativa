package com.nativa.pago_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nativa.pago_service.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long>{
    
}
