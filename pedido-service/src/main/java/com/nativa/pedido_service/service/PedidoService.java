package com.nativa.pedido_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nativa.pedido_service.dto.PedidoResponse;
import com.nativa.pedido_service.mapper.PedidoMapper;
import com.nativa.pedido_service.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoMapper pedidoMapper;
    private final PedidoRepository pedidoRepository;

    public List<PedidoResponse> getAllPedido() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedidoMapper::toResponse)
                .toList();
    }

    public PedidoResponse getPedidoById(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toResponse)
                .orElseThrow(()-> new RuntimeException("Pedido no encontrado"));
    }

    public void deletePedido(Long id) {
        pedidoRepository.deleteById(id);
    }
}
