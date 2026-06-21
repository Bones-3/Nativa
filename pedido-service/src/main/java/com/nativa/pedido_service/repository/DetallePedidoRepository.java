package com.nativa.pedido_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nativa.pedido_service.model.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long>{
    List<DetallePedido> findByPedidoId(Long pedidoId);
}
