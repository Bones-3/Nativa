package com.nativa.pedido_service.service;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoMapper pedidoMapper;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedido;
    private PedidoResponse pedidoResponse;
    private PedidoRequest pedidoRequest;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuarioId(1L);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setTipoEntrega("DELIVERY");
        pedido.setTotalPagar(BigDecimal.valueOf(50000));

        pedidoResponse = PedidoResponse.builder()
                .id(1L).usuarioId(1L).fechaPedido(pedido.getFechaPedido())
                .tipoEntrega("DELIVERY").totalPagar(BigDecimal.valueOf(50000))
                .build();

        pedidoRequest = new PedidoRequest();
        pedidoRequest.setUsuarioId(1L);
        pedidoRequest.setTipoEntrega("DELIVERY");
    }

    @Test
    void getAllPedido_shouldReturnList() {
        Pedido otroPedido = new Pedido();
        otroPedido.setId(2L);
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido, otroPedido));
        when(pedidoMapper.toResponse(pedido)).thenReturn(pedidoResponse);

        PedidoResponse otroResponse = PedidoResponse.builder().id(2L).build();
        when(pedidoMapper.toResponse(otroPedido)).thenReturn(otroResponse);

        List<PedidoResponse> result = pedidoService.getAllPedido();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(pedidoRepository).findAll();
        verify(pedidoMapper).toResponse(pedido);
        verify(pedidoMapper).toResponse(otroPedido);
    }

    @Test
    void getAllPedido_shouldReturnEmptyList() {
        when(pedidoRepository.findAll()).thenReturn(List.of());

        List<PedidoResponse> result = pedidoService.getAllPedido();

        assertThat(result).isEmpty();
        verify(pedidoRepository).findAll();
        verifyNoInteractions(pedidoMapper);
    }

    @Test
    void getPedidoById_shouldReturnPedidoResponse() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toResponse(pedido)).thenReturn(pedidoResponse);

        PedidoResponse result = pedidoService.getPedidoById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTipoEntrega()).isEqualTo("DELIVERY");
        verify(pedidoRepository).findById(1L);
        verify(pedidoMapper).toResponse(pedido);
    }

    @Test
    void getPedidoById_shouldThrowException_whenNotFound() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.getPedidoById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pedido no encontrado");

        verify(pedidoRepository).findById(99L);
        verifyNoInteractions(pedidoMapper);
    }

    @Test
    void createPedidoResponse_shouldReturnPedidoResponse() {
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setId(1L);
        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuario);

        Pedido pedidoEntity = new Pedido();
        when(pedidoMapper.toEntity(pedidoRequest)).thenReturn(pedidoEntity);

        Pedido savedPedido = new Pedido();
        savedPedido.setId(1L);
        savedPedido.setUsuarioId(1L);
        savedPedido.setFechaPedido(LocalDateTime.now());
        savedPedido.setTipoEntrega("DELIVERY");
        savedPedido.setSubtotal(BigDecimal.ZERO);
        savedPedido.setIva(BigDecimal.ZERO);
        savedPedido.setTotalPagar(BigDecimal.ZERO);
        when(pedidoRepository.save(pedidoEntity)).thenReturn(savedPedido);

        PedidoResponse expected = PedidoResponse.builder()
                .id(1L).usuarioId(1L).fechaPedido(savedPedido.getFechaPedido())
                .tipoEntrega("DELIVERY").totalPagar(BigDecimal.ZERO)
                .build();
        when(pedidoMapper.toResponse(savedPedido)).thenReturn(expected);

        PedidoResponse result = pedidoService.createPedidoResponse(pedidoRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTipoEntrega()).isEqualTo("DELIVERY");
        assertThat(result.getTotalPagar()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(usuarioClient).obtenerPorId(1L);
        verify(pedidoMapper).toEntity(pedidoRequest);
        verify(pedidoRepository).save(pedidoEntity);
        verify(pedidoMapper).toResponse(savedPedido);
    }

    @Test
    void createPedidoResponse_shouldThrowException_whenUsuarioNotFound() {
        when(usuarioClient.obtenerPorId(1L)).thenReturn(null);

        assertThatThrownBy(() -> pedidoService.createPedidoResponse(pedidoRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");

        verify(usuarioClient).obtenerPorId(1L);
        verifyNoInteractions(pedidoMapper, pedidoRepository);
    }

    @Test
    void deletePedido_shouldCallDeleteById() {
        pedidoService.deletePedido(1L);
        verify(pedidoRepository).deleteById(1L);
    }

    @Test
    void recalcularTotales_shouldUpdateTotals() {
        Pedido pedidoToUpdate = new Pedido();
        pedidoToUpdate.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoToUpdate));

        DetallePedido detalle1 = new DetallePedido();
        detalle1.setSubtotal(BigDecimal.valueOf(10000));
        DetallePedido detalle2 = new DetallePedido();
        detalle2.setSubtotal(BigDecimal.valueOf(5000));
        when(detallePedidoRepository.findByPedidoId(1L)).thenReturn(List.of(detalle1, detalle2));

        pedidoService.recalcularTotales(1L);

        assertThat(pedidoToUpdate.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(15000));
        assertThat(pedidoToUpdate.getIva()).isEqualByComparingTo(BigDecimal.valueOf(2850));
        assertThat(pedidoToUpdate.getTotalPagar()).isEqualByComparingTo(BigDecimal.valueOf(17850));
        verify(pedidoRepository).findById(1L);
        verify(detallePedidoRepository).findByPedidoId(1L);
        verify(pedidoRepository).save(pedidoToUpdate);
    }

    @Test
    void recalcularTotales_shouldThrowException_whenPedidoNotFound() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.recalcularTotales(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pedido no encontrado");

        verify(pedidoRepository).findById(99L);
        verifyNoInteractions(detallePedidoRepository);
    }
}
