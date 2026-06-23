package com.nativa.pedido_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nativa.pedido_service.client.UsuarioClient;
import com.nativa.pedido_service.client.dto.UsuarioResponse;
import com.nativa.pedido_service.dto.PedidoRequest;
import com.nativa.pedido_service.dto.PedidoResponse;
import com.nativa.pedido_service.exception.ResourceNotFoundException;
import com.nativa.pedido_service.mapper.PedidoMapper;
import com.nativa.pedido_service.model.DetallePedido;
import com.nativa.pedido_service.model.Pedido;
import com.nativa.pedido_service.repository.DetallePedidoRepository;
import com.nativa.pedido_service.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoMapper pedidoMapper;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final UsuarioClient usuarioClient;

    @Transactional(readOnly = true)
    public List<PedidoResponse> getAllPedido() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedidoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse getPedidoById(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Pedido no encontrado"));
    }

    @Transactional
    public PedidoResponse createPedidoResponse(PedidoRequest request) {

    UsuarioResponse usuario =
            usuarioClient.obtenerPorId(request.getUsuarioId());

    if (usuario == null) {
        throw new ResourceNotFoundException("Usuario no encontrado");
    }

    Pedido pedido = pedidoMapper.toEntity(request);

    pedido.setUsuarioId(usuario.getId());

    pedido.setFechaPedido(LocalDateTime.now());

    pedido.setSubtotal(BigDecimal.ZERO);
    pedido.setIva(BigDecimal.ZERO);
    pedido.setTotalPagar(BigDecimal.ZERO);

    return pedidoMapper.toResponse(
            pedidoRepository.save(pedido)
    );
    }

    @Transactional
    public void deletePedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Transactional
    public void recalcularTotales(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        BigDecimal subtotal = detallePedidoRepository
                .findByPedidoId(pedidoId)
                .stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal iva = subtotal.multiply(new BigDecimal("0.19"));

        BigDecimal totalPagar = subtotal.add(iva);

        pedido.setSubtotal(subtotal);
        pedido.setIva(iva);
        pedido.setTotalPagar(totalPagar);

        pedidoRepository.save(pedido);
    }
}
