package com.nativa.reserva_service.service;

import com.nativa.reserva_service.dto.MesaRequest;
import com.nativa.reserva_service.dto.MesaResponse;
import com.nativa.reserva_service.exception.ResourceNotFoundException;
import com.nativa.reserva_service.mapper.MesaMapper;
import com.nativa.reserva_service.model.Mesa;
import com.nativa.reserva_service.repository.MesaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MesaServiceTest {

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private MesaMapper mesaMapper;

    @InjectMocks
    private MesaService mesaService;

    private Mesa mesa;
    private MesaResponse mesaResponse;
    private MesaRequest mesaRequest;

    @BeforeEach
    void setUp() {
        mesa = new Mesa(1L, 1, 4, "Interior", true);

        mesaResponse = MesaResponse.builder()
                .id(1L).numero(1).capacidad(4)
                .ubicacion("Interior").disponible(true)
                .build();

        mesaRequest = new MesaRequest();
        mesaRequest.setNumero(1);
        mesaRequest.setCapacidad(4);
        mesaRequest.setUbicacion("Interior");
    }

    @Test
    void getAllMesas_shouldReturnList() {
        when(mesaRepository.findAll()).thenReturn(List.of(mesa));
        when(mesaMapper.toResponse(mesa)).thenReturn(mesaResponse);

        List<MesaResponse> result = mesaService.getAllMesas();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getNumero()).isEqualTo(1);
        verify(mesaRepository).findAll();
        verify(mesaMapper).toResponse(mesa);
    }

    @Test
    void getAllMesas_shouldReturnEmptyList_whenNoMesas() {
        when(mesaRepository.findAll()).thenReturn(List.of());

        List<MesaResponse> result = mesaService.getAllMesas();

        assertThat(result).isEmpty();
        verify(mesaRepository).findAll();
        verifyNoInteractions(mesaMapper);
    }

    @Test
    void getMesasDisponibles_shouldReturnAvailableMesas() {
        when(mesaRepository.findByDisponibleTrue()).thenReturn(List.of(mesa));
        when(mesaMapper.toResponse(mesa)).thenReturn(mesaResponse);

        List<MesaResponse> result = mesaService.getMesasDisponibles();

        assertThat(result).hasSize(1);
        verify(mesaRepository).findByDisponibleTrue();
        verify(mesaMapper).toResponse(mesa);
    }

    @Test
    void getMesaById_shouldReturnMesa() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(mesaMapper.toResponse(mesa)).thenReturn(mesaResponse);

        MesaResponse result = mesaService.getMesaById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNumero()).isEqualTo(1);
        verify(mesaRepository).findById(1L);
        verify(mesaMapper).toResponse(mesa);
    }

    @Test
    void getMesaById_shouldThrowException_whenNotFound() {
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mesaService.getMesaById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Mesa no encontrada con id: 99");

        verify(mesaRepository).findById(99L);
        verifyNoInteractions(mesaMapper);
    }

    @Test
    void createMesa_shouldReturnSavedMesa() {
        when(mesaMapper.toEntity(mesaRequest)).thenReturn(mesa);
        when(mesaRepository.save(mesa)).thenReturn(mesa);
        when(mesaMapper.toResponse(mesa)).thenReturn(mesaResponse);

        MesaResponse result = mesaService.createMesa(mesaRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDisponible()).isTrue();
        verify(mesaMapper).toEntity(mesaRequest);
        verify(mesaRepository).save(mesa);
        verify(mesaMapper).toResponse(mesa);
    }

    @Test
    void updateMesa_shouldReturnUpdatedMesa() {
        Mesa existingMesa = new Mesa(1L, 1, 4, "Interior", true);
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(existingMesa));
        when(mesaRepository.save(existingMesa)).thenReturn(existingMesa);
        when(mesaMapper.toResponse(existingMesa)).thenReturn(mesaResponse);

        MesaResponse result = mesaService.updateMesa(1L, mesaRequest);

        assertThat(result.getId()).isEqualTo(1L);
        verify(mesaRepository).findById(1L);
        verify(mesaRepository).save(existingMesa);
    }

    @Test
    void updateMesa_shouldThrowException_whenNotFound() {
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mesaService.updateMesa(99L, mesaRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Mesa no encontrada con id: 99");

        verify(mesaRepository).findById(99L);
        verifyNoMoreInteractions(mesaRepository);
    }

    @Test
    void deleteMesa_shouldSetDisponibleToFalse() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));

        mesaService.deleteMesa(1L);

        assertThat(mesa.getDisponible()).isFalse();
        verify(mesaRepository).findById(1L);
        verify(mesaRepository).save(mesa);
    }

    @Test
    void deleteMesa_shouldThrowException_whenNotFound() {
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mesaService.deleteMesa(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Mesa no encontrada con id: 99");

        verify(mesaRepository).findById(99L);
        verifyNoMoreInteractions(mesaRepository);
    }
}
