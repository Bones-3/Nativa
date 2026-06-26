package com.nativa.pedido_service.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nativa.pedido_service.client.ProductoClient;
import com.nativa.pedido_service.client.dto.ProductoResponse;
import com.nativa.pedido_service.dto.DetallePedidoRequest;
import com.nativa.pedido_service.dto.DetallePedidoResponse;
import com.nativa.pedido_service.exception.ResourceNotFoundException;
import com.nativa.pedido_service.mapper.DetallePedidoMapper;
import com.nativa.pedido_service.model.DetallePedido;
import com.nativa.pedido_service.model.Pedido;
import com.nativa.pedido_service.repository.DetallePedidoRepository;
import com.nativa.pedido_service.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public DetallePedidoResponse getDetallePedidoById (Long id) {
        return detallePedidoRepository.findById(id)
                .map(detallePedidoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle pedido no encontrado"));            
    }

@Transactional
public DetallePedidoResponse createDetalleResponse(DetallePedidoRequest detallePedidoRequest) {

    log.info("Iniciando creación de detalle pedido. pedidoId={}, productoId={}, cantidad={}",
            detallePedidoRequest.getPedidoId(),
            detallePedidoRequest.getProductoId(),
            detallePedidoRequest.getCantidad());

    DetallePedido detallePedido = detallePedidoMapper.toEntity(detallePedidoRequest);

    log.info("Consultando producto {}", detallePedidoRequest.getProductoId());

    ProductoResponse producto =
            productoClient.obtenerProductoPorId(detallePedidoRequest.getProductoId());

    log.info("Producto obtenido. disponible={}, precio={}",
            producto.getDisponible(),
            producto.getPrecio());

    if (Boolean.FALSE.equals(producto.getDisponible())) {
        log.warn("Producto {} no disponible", detallePedidoRequest.getProductoId());
        throw new ResourceNotFoundException("El producto no está disponible");
    }

    log.info("Buscando pedido {}", detallePedidoRequest.getPedidoId());

    Pedido pedido = pedidoRepository.findById(detallePedidoRequest.getPedidoId())
            .orElseThrow(() -> {
                log.error("Pedido {} no encontrado", detallePedidoRequest.getPedidoId());
                return new ResourceNotFoundException("Pedido no encontrado");
            });

    detallePedido.setPedido(pedido);
    detallePedido.setPrecioUnitario(producto.getPrecio());

    detallePedido.setSubtotal(
            producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detallePedidoRequest.getCantidad()))
    );

    log.info("Subtotal calculado: {}", detallePedido.getSubtotal());

    DetallePedido guardado = detallePedidoRepository.save(detallePedido);

    log.info("Detalle guardado con id={}", guardado.getId());

    pedidoService.recalcularTotales(
            guardado.getPedido().getId()
    );

    log.info("Totales recalculados para pedido {}",
            guardado.getPedido().getId());

    return detallePedidoMapper.toResponse(guardado);
}

    @Transactional
    public DetallePedidoResponse updateDetallePedidoResponse(Long id, DetallePedidoRequest request) {
        var detallePedido = detallePedidoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("El detalle no se encuentra"));
        ProductoResponse producto = productoClient.obtenerProductoPorId(request.getProductoId());

        if (Boolean.FALSE.equals(producto.getDisponible())) {
            throw new ResourceNotFoundException("El producto no está disponible");
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
                new ResourceNotFoundException("Detalle no encontrado"));
        
                Long pedidoId = detalle.getPedido().getId();

        detallePedidoRepository.delete(detalle);

        pedidoService.recalcularTotales(pedidoId);
    }
}
