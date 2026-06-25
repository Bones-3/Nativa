package com.nativa.resena_service.service;

import com.nativa.resena_service.dto.ResenaRequest;
import com.nativa.resena_service.dto.ResenaResponse;
import com.nativa.resena_service.exception.ResourceNotFoundException;
import com.nativa.resena_service.mapper.ResenaMapper;
import com.nativa.resena_service.model.Resena;
import com.nativa.resena_service.repository.ResenaRepository;
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
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private ResenaMapper resenaMapper;

    @InjectMocks
    private ResenaService resenaService;

    private Resena resena;
    private ResenaResponse resenaResponse;
    private ResenaRequest resenaRequest;

    @BeforeEach
    void setUp() {
        resena = new Resena(1L, 1L, 1L, "Buen producto", 5);
        resenaResponse = ResenaResponse.builder()
                .id(1L).productoId(1L).usuarioId(1L)
                .comentario("Buen producto").calificacion(5).build();
        resenaRequest = new ResenaRequest();
        resenaRequest.setProductoId(1L);
        resenaRequest.setUsuarioId(1L);
        resenaRequest.setComentario("Buen producto");
        resenaRequest.setCalificacion(5);
    }

    @Test
    void getAllResenas_shouldReturnList() {
        when(resenaRepository.findAll()).thenReturn(List.of(resena));
        when(resenaMapper.toResponse(resena)).thenReturn(resenaResponse);

        List<ResenaResponse> result = resenaService.getAllResenas();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getComentario()).isEqualTo("Buen producto");
        verify(resenaRepository).findAll();
        verify(resenaMapper).toResponse(resena);
    }

    @Test
    void getAllResenas_shouldReturnEmptyList_whenNoResenas() {
        when(resenaRepository.findAll()).thenReturn(List.of());

        List<ResenaResponse> result = resenaService.getAllResenas();

        assertThat(result).isEmpty();
        verify(resenaRepository).findAll();
        verifyNoInteractions(resenaMapper);
    }

    @Test
    void getResenaById_shouldReturnResena() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        when(resenaMapper.toResponse(resena)).thenReturn(resenaResponse);

        ResenaResponse result = resenaService.getResenaById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getComentario()).isEqualTo("Buen producto");
        assertThat(result.getCalificacion()).isEqualTo(5);
        verify(resenaRepository).findById(1L);
        verify(resenaMapper).toResponse(resena);
    }

    @Test
    void getResenaById_shouldThrowException_whenNotFound() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resenaService.getResenaById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Rese\u00f1a no encontrada");

        verify(resenaRepository).findById(99L);
        verifyNoInteractions(resenaMapper);
    }

    @Test
    void createResena_shouldReturnSavedResena() {
        when(resenaMapper.toEntity(resenaRequest)).thenReturn(resena);
        when(resenaRepository.save(resena)).thenReturn(resena);
        when(resenaMapper.toResponse(resena)).thenReturn(resenaResponse);

        ResenaResponse result = resenaService.createResena(resenaRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCalificacion()).isEqualTo(5);
        assertThat(result.getComentario()).isEqualTo("Buen producto");
        verify(resenaRepository).save(resena);
        verify(resenaMapper).toEntity(resenaRequest);
        verify(resenaMapper).toResponse(resena);
    }

    @Test
    void resenaPedido_shouldDeleteById() {
        resenaService.resenaPedido(1L);

        verify(resenaRepository).deleteById(1L);
    }
}
