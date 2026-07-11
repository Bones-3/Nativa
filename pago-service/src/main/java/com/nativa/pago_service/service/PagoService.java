package com.nativa.pago_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nativa.pago_service.client.PedidoClient;
import com.nativa.pago_service.client.UsuarioClient;
import com.nativa.pago_service.client.dto.PedidoResponse;
import com.nativa.pago_service.client.dto.UsuarioResponse;
import com.nativa.pago_service.dto.PagoRequest;
import com.nativa.pago_service.dto.PagoResponse;
import com.nativa.pago_service.exception.ResourceNotFoundException;
import com.nativa.pago_service.mapper.PagoMapper;
import com.nativa.pago_service.model.Pago;
import com.nativa.pago_service.repository.PagoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;
    private final UsuarioClient usuarioClient;
    private final PedidoClient pedidoClient;

    public List<PagoResponse> getAllPagos() {
        log.info("Solicitando la lista de todos los pagos");
        return pagoRepository.findAll()
                    .stream()
                    .map(pagoMapper::toResponse)
                    .toList();
    }

    public PagoResponse findById (Long id) {
        log.info("Buscando pago con ID: {}", id);
        return pagoRepository.findById(id)
                    .map(pagoMapper::toResponse)
                    .orElseThrow(() -> {
                        log.warn("No se encontró el pago con ID: {}", id);
                        return new ResourceNotFoundException("Pago no encontrado con id: " + id);
                    });
    }

    public PagoResponse createPago(PagoRequest request) {
        log.info("Iniciando creación de pago para pedido {} y usuario {}", request.getPedido_id(), request.getUsuario_id());

        UsuarioResponse usuario =
            usuarioClient.obtenerPorId(request.getUsuario_id());

        if (usuario == null) {
            log.error("Error al crear pago: usuario {} no encontrado", request.getUsuario_id());
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        PedidoResponse pedido =
            pedidoClient.obtenerPorId(request.getPedido_id());

        if (pedido == null) {
            log.error("Error al crear pago: pedido {} no encontrado", request.getPedido_id());
            throw new ResourceNotFoundException("Pedido no encontrado");
        }

        Pago pago = pagoMapper.toEntity(request);
        pago.setTotal(pedido.getTotalPagar().doubleValue());
        pago.setFechaPago(LocalDateTime.now());

        Pago pagoGuardado = pagoRepository.save(pago);

        log.info("Pago creado exitosamente con ID {}", pagoGuardado.getId());
        return pagoMapper.toResponse(pagoGuardado);
    }
}
