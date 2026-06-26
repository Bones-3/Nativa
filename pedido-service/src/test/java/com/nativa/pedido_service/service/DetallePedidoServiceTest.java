package com.nativa.pedido_service.service;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetallePedidoServiceTest {

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DetallePedidoMapper detallePedidoMapper;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private DetallePedidoService detallePedidoService;

    private DetallePedido detalle;
    private DetallePedidoResponse detalleResponse;
    private DetallePedidoRequest detalleRequest;
    private Pedido pedido;
    private ProductoResponse producto;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L);

        detalle = new DetallePedido();
        detalle.setId(1L);
        detalle.setProductoId(10L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(BigDecimal.valueOf(5200));
        detalle.setSubtotal(BigDecimal.valueOf(10400));
        detalle.setPedido(pedido);

        detalleResponse = DetallePedidoResponse.builder()
                .id(1L).productoId(10L).pedidoId(1L).cantidad(2)
                .precioUnitario(BigDecimal.valueOf(5200))
                .subtotal(BigDecimal.valueOf(10400))
                .build();

        detalleRequest = new DetallePedidoRequest();
        detalleRequest.setPedidoId(1L);
        detalleRequest.setProductoId(10L);
        detalleRequest.setCantidad(2);

        producto = new ProductoResponse();
        producto.setId(10L);
        producto.setPrecio(BigDecimal.valueOf(5200));
        producto.setDisponible(true);
    }

    @Test
    void getAllDetallePedido_shouldReturnList() {
        DetallePedido otroDetalle = new DetallePedido();
        otroDetalle.setId(2L);
        when(detallePedidoRepository.findAll()).thenReturn(List.of(detalle, otroDetalle));
        when(detallePedidoMapper.toResponse(detalle)).thenReturn(detalleResponse);

        DetallePedidoResponse otroResponse = DetallePedidoResponse.builder().id(2L).build();
        when(detallePedidoMapper.toResponse(otroDetalle)).thenReturn(otroResponse);

        var result = detallePedidoService.getAllDetallePedido();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(detallePedidoRepository).findAll();
    }

    @Test
    void getAllDetallePedido_shouldReturnEmptyList() {
        when(detallePedidoRepository.findAll()).thenReturn(List.of());

        var result = detallePedidoService.getAllDetallePedido();

        assertThat(result).isEmpty();
        verify(detallePedidoRepository).findAll();
        verifyNoInteractions(detallePedidoMapper);
    }

    @Test
    void getDetallePedidoById_shouldReturnDetallePedidoResponse() {
        when(detallePedidoRepository.findById(1L)).thenReturn(Optional.of(detalle));
        when(detallePedidoMapper.toResponse(detalle)).thenReturn(detalleResponse);

        var result = detallePedidoService.getDetallePedidoById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductoId()).isEqualTo(10L);
        assertThat(result.getCantidad()).isEqualTo(2);
        verify(detallePedidoRepository).findById(1L);
        verify(detallePedidoMapper).toResponse(detalle);
    }

    @Test
    void getDetallePedidoById_shouldThrowException_whenNotFound() {
        when(detallePedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> detallePedidoService.getDetallePedidoById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Detalle pedido no encontrado");

        verify(detallePedidoRepository).findById(99L);
        verifyNoInteractions(detallePedidoMapper);
    }

    @Test
    void createDetalleResponse_shouldReturnDetallePedidoResponse() {
        DetallePedido entity = new DetallePedido();
        when(detallePedidoMapper.toEntity(detalleRequest)).thenReturn(entity);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        DetallePedido guardado = new DetallePedido();
        guardado.setId(1L);
        guardado.setProductoId(10L);
        guardado.setCantidad(2);
        guardado.setPrecioUnitario(BigDecimal.valueOf(5200));
        guardado.setSubtotal(BigDecimal.valueOf(10400));
        guardado.setPedido(pedido);
        when(detallePedidoRepository.save(entity)).thenReturn(guardado);

        DetallePedidoResponse expected = DetallePedidoResponse.builder()
                .id(1L).productoId(10L).pedidoId(1L).cantidad(2)
                .precioUnitario(BigDecimal.valueOf(5200))
                .subtotal(BigDecimal.valueOf(10400))
                .build();
        when(detallePedidoMapper.toResponse(guardado)).thenReturn(expected);

        var result = detallePedidoService.createDetalleResponse(detalleRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(10400));
        verify(productoClient).obtenerProductoPorId(10L);
        verify(pedidoRepository).findById(1L);
        verify(detallePedidoRepository).save(entity);
        verify(pedidoService).recalcularTotales(1L);
    }

    @Test
    void createDetalleResponse_shouldThrowException_whenProductoNotAvailable() {
        producto.setDisponible(false);
        when(detallePedidoMapper.toEntity(detalleRequest)).thenReturn(new DetallePedido());
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);

        assertThatThrownBy(() -> detallePedidoService.createDetalleResponse(detalleRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("El producto no está disponible");

        verify(productoClient).obtenerProductoPorId(10L);
        verifyNoInteractions(pedidoRepository, detallePedidoRepository);
    }

    @Test
    void createDetalleResponse_shouldThrowException_whenPedidoNotFound() {
        when(detallePedidoMapper.toEntity(detalleRequest)).thenReturn(new DetallePedido());
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> detallePedidoService.createDetalleResponse(detalleRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pedido no encontrado");

        verify(productoClient).obtenerProductoPorId(10L);
        verify(pedidoRepository).findById(1L);
        verifyNoInteractions(detallePedidoRepository);
    }

    @Test
    void updateDetallePedidoResponse_shouldReturnUpdated() {
        when(detallePedidoRepository.findById(1L)).thenReturn(Optional.of(detalle));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);

        DetallePedido guardado = new DetallePedido();
        guardado.setId(1L);
        guardado.setProductoId(10L);
        guardado.setCantidad(2);
        guardado.setPrecioUnitario(BigDecimal.valueOf(5200));
        guardado.setSubtotal(BigDecimal.valueOf(10400));
        guardado.setPedido(pedido);
        when(detallePedidoRepository.save(detalle)).thenReturn(guardado);

        DetallePedidoResponse expected = DetallePedidoResponse.builder()
                .id(1L).productoId(10L).pedidoId(1L).cantidad(2)
                .precioUnitario(BigDecimal.valueOf(5200))
                .subtotal(BigDecimal.valueOf(10400))
                .build();
        when(detallePedidoMapper.toResponse(guardado)).thenReturn(expected);

        var result = detallePedidoService.updateDetallePedidoResponse(1L, detalleRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(10400));
        verify(detallePedidoRepository).findById(1L);
        verify(productoClient).obtenerProductoPorId(10L);
        verify(detallePedidoRepository).save(detalle);
        verify(pedidoService).recalcularTotales(1L);
    }

    @Test
    void updateDetallePedidoResponse_shouldThrowException_whenDetalleNotFound() {
        when(detallePedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> detallePedidoService.updateDetallePedidoResponse(99L, detalleRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("El detalle no se encuentra");

        verify(detallePedidoRepository).findById(99L);
        verifyNoInteractions(productoClient);
    }

    @Test
    void updateDetallePedidoResponse_shouldThrowException_whenProductoNotAvailable() {
        producto.setDisponible(false);
        when(detallePedidoRepository.findById(1L)).thenReturn(Optional.of(detalle));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);

        assertThatThrownBy(() -> detallePedidoService.updateDetallePedidoResponse(1L, detalleRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("El producto no está disponible");

        verify(detallePedidoRepository).findById(1L);
        verify(productoClient).obtenerProductoPorId(10L);
        verifyNoMoreInteractions(detallePedidoRepository);
        verifyNoInteractions(pedidoRepository);
    }

    @Test
    void deleteDetallePedido_shouldDelete() {
        when(detallePedidoRepository.findById(1L)).thenReturn(Optional.of(detalle));

        detallePedidoService.deleteDetallePedido(1L);

        verify(detallePedidoRepository).findById(1L);
        verify(detallePedidoRepository).delete(detalle);
        verify(pedidoService).recalcularTotales(1L);
    }

    @Test
    void deleteDetallePedido_shouldThrowException_whenNotFound() {
        when(detallePedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> detallePedidoService.deleteDetallePedido(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Detalle no encontrado");

        verify(detallePedidoRepository).findById(99L);
        verifyNoMoreInteractions(detallePedidoRepository);
        verifyNoInteractions(pedidoService);
    }
}
