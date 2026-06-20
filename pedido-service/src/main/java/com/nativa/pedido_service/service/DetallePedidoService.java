package com.nativa.pedido_service.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nativa.pedido_service.client.ProductoClient;
import com.nativa.pedido_service.client.dto.ProductoResponse;
import com.nativa.pedido_service.dto.DetallePedidoRequest;
import com.nativa.pedido_service.dto.DetallePedidoResponse;
import com.nativa.pedido_service.mapper.DetallePedidoMapper;
import com.nativa.pedido_service.model.DetallePedido;
import com.nativa.pedido_service.model.Pedido;
import com.nativa.pedido_service.repository.DetallePedidoRepository;
import com.nativa.pedido_service.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoMapper detallePedidoMapper;
    private final PedidoService pedidoService;
    private final ProductoClient productoClient;

    @Transactional(readOnly = true)
    public List<DetallePedidoResponse> getAllDetallePedido() {
        return detallePedidoRepository.findAll()
                .stream()
                .map(detallePedidoMapper::toResponse)
                .toList(); 
    }
    
    @Transactional(readOnly = true)
    public DetallePedidoResponse getDetallePedidoById (Long id) {
        return detallePedidoRepository.findById(id)
                .map(detallePedidoMapper::toResponse)
                .orElseThrow(()-> new RuntimeException("El detalle no se encontra"));
    }

    @Transactional
    public DetallePedidoResponse createDetalleResponse(Long pedidoId, DetallePedidoRequest detallePedidoRequest) {
        var detallePedido = detallePedidoMapper.toEntity(detallePedidoRequest);
        ProductoResponse producto = productoClient.obtenerProductoPorId(detallePedidoRequest.getProductoId());

        if (Boolean.FALSE.equals(producto.getDisponible())) {
            throw new RuntimeException("El producto no está disponible");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        detallePedido.setPedido(pedido);
        detallePedido.setPrecioUnitario(producto.getPrecio());

        detallePedido.setSubtotal(
                producto.getPrecio()
                        .multiply(BigDecimal.valueOf(detallePedidoRequest.getCantidad()))
        );

        DetallePedido guardado = detallePedidoRepository.save(detallePedido);

        pedidoService.recalcularTotales(
            guardado.getPedido().getId()
        );

        return detallePedidoMapper.toResponse(guardado);
    }

    @Transactional
    public DetallePedidoResponse updateDetallePedidoResponse(Long id, DetallePedidoRequest request) {
        var detallePedido = detallePedidoRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("El detalle no se encuentra"));
        ProductoResponse producto = productoClient.obtenerProductoPorId(request.getProductoId());

        if (Boolean.FALSE.equals(producto.getDisponible())) {
            throw new RuntimeException("El producto no está disponible");
        }

        detallePedido.setProductoId(request.getProductoId());
        detallePedido.setCantidad(request.getCantidad());
        detallePedido.setPrecioUnitario(producto.getPrecio());

        detallePedido.setSubtotal(
                producto.getPrecio()
                        .multiply(BigDecimal.valueOf(request.getCantidad()))
        );

        DetallePedido guardado = detallePedidoRepository.save(detallePedido);

        pedidoService.recalcularTotales(
            guardado.getPedido().getId()
        );
        return detallePedidoMapper.toResponse(guardado);
    }

    @Transactional
    public void deleteDetallePedido(Long id) {
        DetallePedido detalle = detallePedidoRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Detalle no encontrado"));
        
                Long pedidoId = detalle.getPedido().getId();

        detallePedidoRepository.delete(detalle);

        pedidoService.recalcularTotales(pedidoId);
    }
}
