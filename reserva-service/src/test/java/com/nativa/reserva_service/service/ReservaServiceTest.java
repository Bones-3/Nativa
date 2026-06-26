package com.nativa.reserva_service.service;

import com.nativa.reserva_service.dto.ReservaRequest;
import com.nativa.reserva_service.dto.ReservaResponse;
import com.nativa.reserva_service.exception.BadRequestException;
import com.nativa.reserva_service.exception.ResourceNotFoundException;
import com.nativa.reserva_service.mapper.ReservaMapper;
import com.nativa.reserva_service.model.Mesa;
import com.nativa.reserva_service.model.Reserva;
import com.nativa.reserva_service.repository.MesaRepository;
import com.nativa.reserva_service.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private ReservaMapper reservaMapper;

    @InjectMocks
    private ReservaService reservaService;

    private Mesa mesa;
    private Reserva reserva;
    private ReservaResponse reservaResponse;
    private ReservaRequest reservaRequest;

    @BeforeEach
    void setUp() {
        mesa = new Mesa(1L, 1, 4, "Interior", true);

        reserva = new Reserva();
        reserva.setId(1L);
        reserva.setNombreCliente("Juan Pérez");
        reserva.setTelefono("+56 9 1234 5678");
        reserva.setEmail("juan@email.com");
        reserva.setFecha(LocalDate.of(2026, 7, 15));
        reserva.setHora(LocalTime.of(20, 0));
        reserva.setCantidadPersonas(4);
        reserva.setEstado("PENDIENTE");
        reserva.setFechaCreacion(LocalDateTime.of(2026, 7, 14, 10, 0));
        reserva.setMesa(mesa);

        reservaResponse = ReservaResponse.builder()
                .id(1L).nombreCliente("Juan Pérez")
                .telefono("+56 9 1234 5678").email("juan@email.com")
                .fecha(LocalDate.of(2026, 7, 15)).hora(LocalTime.of(20, 0))
                .cantidadPersonas(4).estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.of(2026, 7, 14, 10, 0))
                .mesaId(1L).mesaNumero(1).mesaCapacidad(4).mesaUbicacion("Interior")
                .build();

        reservaRequest = new ReservaRequest();
        reservaRequest.setNombreCliente("Juan Pérez");
        reservaRequest.setTelefono("+56 9 1234 5678");
        reservaRequest.setEmail("juan@email.com");
        reservaRequest.setFecha(LocalDate.of(2026, 7, 15));
        reservaRequest.setHora(LocalTime.of(20, 0));
        reservaRequest.setCantidadPersonas(4);
        reservaRequest.setMesaId(1L);
    }

    @Test
    void getAllReservas_shouldReturnList() {
        when(reservaRepository.findByOrderByFechaCreacionDesc()).thenReturn(List.of(reserva));
        when(reservaMapper.toResponse(reserva)).thenReturn(reservaResponse);

        List<ReservaResponse> result = reservaService.getAllReservas();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getNombreCliente()).isEqualTo("Juan Pérez");
        verify(reservaRepository).findByOrderByFechaCreacionDesc();
        verify(reservaMapper).toResponse(reserva);
    }

    @Test
    void getAllReservas_shouldReturnEmptyList_whenNoReservas() {
        when(reservaRepository.findByOrderByFechaCreacionDesc()).thenReturn(List.of());

        List<ReservaResponse> result = reservaService.getAllReservas();

        assertThat(result).isEmpty();
        verify(reservaRepository).findByOrderByFechaCreacionDesc();
        verifyNoInteractions(reservaMapper);
    }

    @Test
    void getReservaById_shouldReturnReserva() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaMapper.toResponse(reserva)).thenReturn(reservaResponse);

        ReservaResponse result = reservaService.getReservaById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombreCliente()).isEqualTo("Juan Pérez");
        verify(reservaRepository).findById(1L);
        verify(reservaMapper).toResponse(reserva);
    }

    @Test
    void getReservaById_shouldThrowException_whenNotFound() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.getReservaById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Reserva no encontrada con id: 99");

        verify(reservaRepository).findById(99L);
        verifyNoInteractions(reservaMapper);
    }

    @Test
    void createReserva_shouldReturnSavedReserva() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(reservaRepository.findByMesaIdAndFechaAndHoraBetween(eq(1L), eq(LocalDate.of(2026, 7, 15)), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(List.of());
        when(reservaMapper.toEntity(reservaRequest)).thenReturn(reserva);
        when(reservaRepository.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponse(reserva)).thenReturn(reservaResponse);

        ReservaResponse result = reservaService.createReserva(reservaRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEstado()).isEqualTo("PENDIENTE");
        verify(mesaRepository).findById(1L);
        verify(reservaMapper).toEntity(reservaRequest);
        verify(reservaRepository).save(reserva);
        verify(reservaMapper).toResponse(reserva);
    }

    @Test
    void createReserva_shouldThrowException_whenMesaNotFound() {
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());
        reservaRequest.setMesaId(99L);

        assertThatThrownBy(() -> reservaService.createReserva(reservaRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Mesa no encontrada con id: 99");

        verify(mesaRepository).findById(99L);
        verifyNoMoreInteractions(mesaRepository);
        verifyNoInteractions(reservaRepository);
    }

    @Test
    void createReserva_shouldThrowException_whenMesaNotAvailable() {
        mesa.setDisponible(false);
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));

        assertThatThrownBy(() -> reservaService.createReserva(reservaRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("La mesa no está disponible para reservas");

        verify(mesaRepository).findById(1L);
        verifyNoInteractions(reservaRepository);
    }

    @Test
    void createReserva_shouldThrowException_whenCapacityExceeded() {
        reservaRequest.setCantidadPersonas(6);
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));

        assertThatThrownBy(() -> reservaService.createReserva(reservaRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("excede la capacidad");

        verify(mesaRepository).findById(1L);
        verifyNoInteractions(reservaRepository);
    }

    @Test
    void createReserva_shouldThrowException_whenTimeConflict() {
        Reserva conflict = new Reserva();
        conflict.setId(2L);
        conflict.setEstado("PENDIENTE");

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(reservaRepository.findByMesaIdAndFechaAndHoraBetween(eq(1L), eq(LocalDate.of(2026, 7, 15)), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(List.of(conflict));

        assertThatThrownBy(() -> reservaService.createReserva(reservaRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("rango cercano");

        verify(mesaRepository).findById(1L);
        verify(reservaRepository).findByMesaIdAndFechaAndHoraBetween(any(), any(), any(), any());
        verifyNoMoreInteractions(reservaRepository);
    }

    @Test
    void createReserva_shouldIgnoreConflicts_whenCancelled() {
        Reserva cancelled = new Reserva();
        cancelled.setId(2L);
        cancelled.setEstado("CANCELADA");

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(reservaRepository.findByMesaIdAndFechaAndHoraBetween(eq(1L), eq(LocalDate.of(2026, 7, 15)), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(List.of(cancelled));
        when(reservaMapper.toEntity(reservaRequest)).thenReturn(reserva);
        when(reservaRepository.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponse(reserva)).thenReturn(reservaResponse);

        ReservaResponse result = reservaService.createReserva(reservaRequest);

        assertThat(result.getId()).isEqualTo(1L);
        verify(reservaRepository).save(reserva);
    }

    @Test
    void updateReserva_shouldReturnUpdatedReserva() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(reservaRepository.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponse(reserva)).thenReturn(reservaResponse);

        ReservaResponse result = reservaService.updateReserva(1L, reservaRequest);

        assertThat(result.getId()).isEqualTo(1L);
        verify(reservaRepository).findById(1L);
        verify(mesaRepository).findById(1L);
        verify(reservaRepository).save(reserva);
    }

    @Test
    void updateReserva_shouldThrowException_whenReservaNotFound() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.updateReserva(99L, reservaRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Reserva no encontrada con id: 99");

        verify(reservaRepository).findById(99L);
        verifyNoMoreInteractions(reservaRepository);
        verifyNoInteractions(mesaRepository);
    }

    @Test
    void updateReserva_shouldThrowException_whenMesaNotFound() {
        reservaRequest.setMesaId(99L);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.updateReserva(1L, reservaRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Mesa no encontrada con id: 99");

        verify(reservaRepository).findById(1L);
        verify(mesaRepository).findById(99L);
    }

    @Test
    void cancelReserva_shouldSetEstadoToCancelada() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        reservaService.cancelReserva(1L);

        assertThat(reserva.getEstado()).isEqualTo("CANCELADA");
        verify(reservaRepository).findById(1L);
        verify(reservaRepository).save(reserva);
    }

    @Test
    void cancelReserva_shouldThrowException_whenNotFound() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.cancelReserva(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Reserva no encontrada con id: 99");

        verify(reservaRepository).findById(99L);
        verifyNoMoreInteractions(reservaRepository);
    }
}
