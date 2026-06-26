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

@Service
@RequiredArgsConstructor
public class PagoService {
    
    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;
    private final UsuarioClient usuarioClient;
    private final PedidoClient pedidoClient;

    public List<PagoResponse> getAllPagos() {
        return pagoRepository.findAll()
                    .stream()
                    .map(pagoMapper::toResponse)
                    .toList();
    }

    public PagoResponse findById (Long id) {
        return pagoRepository.findById(id)
                    .map(pagoMapper::toResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
    }

    public PagoResponse createPago(PagoRequest request) {
        UsuarioResponse usuario =
            usuarioClient.obtenerPorId(request.getUsuario_id());

        if (usuario == null) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        
        PedidoResponse pedido =
            pedidoClient.obtenerPorId(request.getPedido_id());

        if (pedido == null) {
            throw new ResourceNotFoundException("Pedido no encontrado");
        }

        Pago pago = pagoMapper.toEntity(request);
        pago.setTotal(pedido.getTotalPagar().doubleValue());
        pago.setFechaPago(LocalDateTime.now());

        Pago pagoGuardado = pagoRepository.save(pago);

        return pagoMapper.toResponse(pagoGuardado);
    }    
}
