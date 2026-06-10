package com.nativa.pedido_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nativa.pedido_service.dto.DetallePedidoRequest;
import com.nativa.pedido_service.dto.DetallePedidoResponse;
import com.nativa.pedido_service.mapper.DetallePedidoMapper;
import com.nativa.pedido_service.repository.DetallePedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final DetallePedidoMapper detallePedidoMapper;

    public List<DetallePedidoResponse> getAllDetallePedido() {
        return detallePedidoRepository.findAll()
                .stream()
                .map(detallePedidoMapper::toResponse)
                .toList(); 
    }
    
    public DetallePedidoResponse getDetallePedidoById (Long id) {
        return detallePedidoRepository.findById(id)
                .map(detallePedidoMapper::toResponse)
                .orElseThrow(()-> new RuntimeException("El detalle no se encontra"));
    }

    public DetallePedidoResponse createDetalleResponse(Long id, DetallePedidoRequest detallePedidoRequest) {
        var detallepedido = detallePedidoMapper.toEntity(detallePedidoRequest);
        return detallePedidoMapper.toResponse(detallePedidoRepository.save(detallepedido));
    }

    public DetallePedidoResponse updateDetallePedidoResponse(Long id, DetallePedidoRequest request) {
        var detallepedido = detallePedidoRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("El detalle no se encuentra"));
        
        detallepedido.setProductoId(request.getProductoId());
        detallepedido.setCantidad(request.getCantidad());

        return detallePedidoMapper.toResponse(detallePedidoRepository.save(detallepedido));
    }

    public void deleteDetallePedido(Long id) {
        detallePedidoRepository.deleteById(id);
    }
}
