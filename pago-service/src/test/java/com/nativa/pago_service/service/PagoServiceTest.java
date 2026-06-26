package com.nativa.pago_service.service;

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
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PagoMapper pagoMapper;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private PagoService pagoService;

    private Pago pago;
    private PagoResponse pagoResponse;
    private PagoRequest pagoRequest;

    @BeforeEach
    void setUp() {
        pago = new Pago(1L, 1L, 1L, 100.0, "TARJETA", LocalDateTime.now());

        pagoResponse = PagoResponse.builder()
                .id(1L).pedido_id(1L).usuario_id(1L).total(100.0)
                .metodoPago("TARJETA").fechaPago(pago.getFechaPago())
                .build();

        pagoRequest = new PagoRequest();
        pagoRequest.setPedido_id(1L);
        pagoRequest.setUsuario_id(1L);
        pagoRequest.setMetodoPago("TARJETA");
    }

    @Test
    void getAllPagos_shouldReturnList() {
        Pago otroPago = new Pago(2L, 2L, 2L, 200.0, "EFECTIVO", LocalDateTime.now());
        when(pagoRepository.findAll()).thenReturn(List.of(pago, otroPago));
        when(pagoMapper.toResponse(pago)).thenReturn(pagoResponse);

        PagoResponse otroResponse = PagoResponse.builder()
                .id(2L).pedido_id(2L).usuario_id(2L).total(200.0)
                .metodoPago("EFECTIVO").fechaPago(otroPago.getFechaPago())
                .build();
        when(pagoMapper.toResponse(otroPago)).thenReturn(otroResponse);

        List<PagoResponse> result = pagoService.getAllPagos();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(pagoRepository).findAll();
        verify(pagoMapper).toResponse(pago);
        verify(pagoMapper).toResponse(otroPago);
    }

    @Test
    void getAllPagos_shouldReturnEmptyList() {
        when(pagoRepository.findAll()).thenReturn(List.of());

        List<PagoResponse> result = pagoService.getAllPagos();

        assertThat(result).isEmpty();
        verify(pagoRepository).findAll();
        verifyNoInteractions(pagoMapper);
    }

    @Test
    void findById_shouldReturnPagoResponse() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoMapper.toResponse(pago)).thenReturn(pagoResponse);

        PagoResponse result = pagoService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotal()).isEqualTo(100.0);
        verify(pagoRepository).findById(1L);
        verify(pagoMapper).toResponse(pago);
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pago no encontrado con id: 99");

        verify(pagoRepository).findById(99L);
        verifyNoInteractions(pagoMapper);
    }

    @Test
    void createPago_shouldReturnPagoResponse() {
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setId(1L);
        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuario);

        PedidoResponse pedido = new PedidoResponse();
        pedido.setId(1L);
        pedido.setTotalPagar(new BigDecimal("100.00"));
        when(pedidoClient.obtenerPorId(1L)).thenReturn(pedido);

        Pago pagoEntity = new Pago();
        when(pagoMapper.toEntity(pagoRequest)).thenReturn(pagoEntity);

        Pago savedPago = new Pago(1L, 1L, 1L, 100.0, "TARJETA", LocalDateTime.now());
        when(pagoRepository.save(pagoEntity)).thenReturn(savedPago);

        PagoResponse expectedResponse = PagoResponse.builder()
                .id(1L).pedido_id(1L).usuario_id(1L).total(100.0)
                .metodoPago("TARJETA").fechaPago(savedPago.getFechaPago())
                .build();
        when(pagoMapper.toResponse(savedPago)).thenReturn(expectedResponse);

        PagoResponse result = pagoService.createPago(pagoRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotal()).isEqualTo(100.0);
        verify(usuarioClient).obtenerPorId(1L);
        verify(pedidoClient).obtenerPorId(1L);
        verify(pagoMapper).toEntity(pagoRequest);
        verify(pagoRepository).save(pagoEntity);
        verify(pagoMapper).toResponse(savedPago);
        verifyNoMoreInteractions(usuarioClient, pedidoClient, pagoMapper, pagoRepository);
    }

    @Test
    void createPago_shouldThrowException_whenUsuarioNotFound() {
        when(usuarioClient.obtenerPorId(1L)).thenReturn(null);

        assertThatThrownBy(() -> pagoService.createPago(pagoRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");

        verify(usuarioClient).obtenerPorId(1L);
        verifyNoInteractions(pedidoClient, pagoMapper, pagoRepository);
    }

    @Test
    void createPago_shouldThrowException_whenPedidoNotFound() {
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setId(1L);
        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuario);

        when(pedidoClient.obtenerPorId(1L)).thenReturn(null);

        assertThatThrownBy(() -> pagoService.createPago(pagoRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pedido no encontrado");

        verify(usuarioClient).obtenerPorId(1L);
        verify(pedidoClient).obtenerPorId(1L);
        verifyNoInteractions(pagoMapper, pagoRepository);
    }
}
