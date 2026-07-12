package com.nativa.horario_service.service;

import com.nativa.horario_service.dto.HorarioRequest;
import com.nativa.horario_service.dto.HorarioResponse;
import com.nativa.horario_service.exception.ResourceNotFoundException;
import com.nativa.horario_service.mapper.HorarioMapper;
import com.nativa.horario_service.model.Horario;
import com.nativa.horario_service.repository.HorarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioServiceTest {

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private HorarioMapper horarioMapper;

    @InjectMocks
    private HorarioService horarioService;

    private Horario horario;
    private HorarioResponse horarioResponse;
    private HorarioRequest horarioRequest;

    @BeforeEach
    void setUp() {
        horario = new Horario(1L, "Lunes, Martes, Miercoles, Jueves, Viernes", LocalTime.of(9, 0), LocalTime.of(22, 0), false);
        horarioResponse = HorarioResponse.builder()
                .id(1L)
                .diaSemana("Lunes, Martes, Miercoles, Jueves, Viernes")
                .horaApertura(LocalTime.of(9, 0))
                .horaCierre(LocalTime.of(22, 0))
                .cerrado(false)
                .build();
        horarioRequest = new HorarioRequest();
        horarioRequest.setDiaSemana("Lunes, Martes, Miercoles, Jueves, Viernes");
        horarioRequest.setHoraApertura(LocalTime.of(9, 0));
        horarioRequest.setHoraCierre(LocalTime.of(22, 0));
        horarioRequest.setCerrado(false);
    }

    @Test
    void getAllHorarios_shouldReturnList() {
        when(horarioRepository.findAll()).thenReturn(List.of(horario));
        when(horarioMapper.toResponse(horario)).thenReturn(horarioResponse);

        List<HorarioResponse> result = horarioService.getAllHorarios();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getDiaSemana()).isEqualTo("Lunes, Martes, Miercoles, Jueves, Viernes");
        assertThat(result.getFirst().getHoraApertura()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.getFirst().getHoraCierre()).isEqualTo(LocalTime.of(22, 0));
        assertThat(result.getFirst().getCerrado()).isEqualTo(false);
        verify(horarioRepository).findAll();
        verify(horarioMapper).toResponse(horario);
    }

    @Test
    void getAllHorarios_shouldReturnEmptyList_whenNoHorarios() {
        when(horarioRepository.findAll()).thenReturn(List.of());

        List<HorarioResponse> result = horarioService.getAllHorarios();

        assertThat(result).isEmpty();
        verify(horarioRepository).findAll();
        verifyNoInteractions(horarioMapper);
    }

    @Test
    void getHorarioById_shouldReturnHorario() {
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(horarioMapper.toResponse(horario)).thenReturn(horarioResponse);

        HorarioResponse result = horarioService.getHorarioById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDiaSemana()).isEqualTo("Lunes, Martes, Miercoles, Jueves, Viernes");
        assertThat(result.getHoraApertura()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.getHoraCierre()).isEqualTo(LocalTime.of(22, 0));
        assertThat(result.getCerrado()).isEqualTo(false);
        verify(horarioRepository).findById(1L);
        verify(horarioMapper).toResponse(horario);
    }

    @Test
    void getHorarioById_shouldThrowException_whenNotFound() {
        when(horarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> horarioService.getHorarioById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Horario no encontrado");

        verify(horarioRepository).findById(99L);
        verifyNoInteractions(horarioMapper);
    }

    @Test
    void createHorario_shouldReturnSavedHorario() {
        when(horarioMapper.toEntity(horarioRequest)).thenReturn(horario);
        when(horarioRepository.save(horario)).thenReturn(horario);
        when(horarioMapper.toResponse(horario)).thenReturn(horarioResponse);

        HorarioResponse result = horarioService.createHorario(horarioRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDiaSemana()).isEqualTo("Lunes, Martes, Miercoles, Jueves, Viernes");
        assertThat(result.getHoraApertura()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.getHoraCierre()).isEqualTo(LocalTime.of(22, 0));
        assertThat(result.getCerrado()).isEqualTo(false);
        verify(horarioRepository).save(horario);
        verify(horarioMapper).toEntity(horarioRequest);
        verify(horarioMapper).toResponse(horario);
    }

    @Test
    void deleteHorario_shouldDisableById() {
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));

        horarioService.deleteHorario(1L);

        assertThat(horario.getCerrado()).isTrue();
        verify(horarioRepository).findById(1L);
        verify(horarioRepository).save(horario);
    }

    @Test
    void deleteHorario_shouldThrowException_whenNotFound() {
        when(horarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> horarioService.deleteHorario(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Horario no encontrado");

        verify(horarioRepository).findById(99L);
        verify(horarioRepository, never()).save(any());
    }
}
