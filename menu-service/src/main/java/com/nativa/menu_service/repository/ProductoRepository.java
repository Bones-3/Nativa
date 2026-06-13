package com.nativa.menu_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nativa.menu_service.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByDisponibleTrue();
    // En tu ProductoRepository:
    List<Producto> findByDisponibleTrueAndCategoria_NombreIgnoreCase(String nombreCategoria);
}
