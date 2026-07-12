package com.nativa.menu_service.service;

import com.nativa.menu_service.dto.CategoriaRequest;
import com.nativa.menu_service.dto.CategoriaResponse;
import com.nativa.menu_service.exception.ResourceNotFoundException;
import com.nativa.menu_service.mapper.CategoriaMapper;
import com.nativa.menu_service.model.Categoria;
import com.nativa.menu_service.repository.CategoriaRepository;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaResponse categoriaResponse;
    private CategoriaRequest categoriaRequest;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Entradas", true);
        categoriaResponse = CategoriaResponse.builder()
                .id(1L).nombre("Entradas").disponible(true).build();
        categoriaRequest = new CategoriaRequest();
        categoriaRequest.setNombre("Entradas");
    }

    @Test
    void getCategoriasDisponible_shouldReturnList() {
        // Given
        when(categoriaRepository.findByDisponibleTrue()).thenReturn(List.of(categoria));
        when(categoriaMapper.toResponse(categoria)).thenReturn(categoriaResponse);

        // When
        List<CategoriaResponse> result = categoriaService.getCategoriasDisponible();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getNombre()).isEqualTo("Entradas");
        verify(categoriaRepository).findByDisponibleTrue();
        verify(categoriaMapper).toResponse(categoria);
    }

    @Test
    void getCategoriasDisponible_shouldReturnEmptyList_whenNoCategorias() {
        // Given
        when(categoriaRepository.findByDisponibleTrue()).thenReturn(List.of());

        // When
        List<CategoriaResponse> result = categoriaService.getCategoriasDisponible();

        // Then
        assertThat(result).isEmpty();
        verify(categoriaRepository).findByDisponibleTrue();
        verifyNoInteractions(categoriaMapper);
    }

    @Test
    void getAllCategorias_shouldReturnList() {
        // Given
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));
        when(categoriaMapper.toResponse(categoria)).thenReturn(categoriaResponse);

        // When
        List<CategoriaResponse> result = categoriaService.getAllCategorias();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNombre()).isEqualTo("Entradas");
        verify(categoriaRepository).findAll();
        verify(categoriaMapper).toResponse(categoria);
    }

    @Test
    void getCategoriaById_shouldReturnCategoria() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaMapper.toResponse(categoria)).thenReturn(categoriaResponse);

        // When
        CategoriaResponse result = categoriaService.getCategoriaById(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Entradas");
        verify(categoriaRepository).findById(1L);
        verify(categoriaMapper).toResponse(categoria);
    }

    @Test
    void getCategoriaById_shouldThrowException_whenNotFound() {
        // Given
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> categoriaService.getCategoriaById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoría no encontrada");

        verify(categoriaRepository).findById(99L);
        verifyNoInteractions(categoriaMapper);
    }

    @Test
    void createCategoria_shouldReturnSavedCategoria() {
        // Given
        when(categoriaMapper.toEntity(categoriaRequest)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toResponse(categoria)).thenReturn(categoriaResponse);

        // When
        CategoriaResponse result = categoriaService.createCategoria(categoriaRequest);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Entradas");
        verify(categoriaRepository).save(categoria);
        verify(categoriaMapper).toEntity(categoriaRequest);
        verify(categoriaMapper).toResponse(categoria);
    }

    @Test
    void updateCategoria_shouldReturnUpdatedCategoria() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toResponse(categoria)).thenReturn(categoriaResponse);

        // When
        CategoriaResponse result = categoriaService.updateCategoria(1L, categoriaRequest);

        // Then
        assertThat(result.getNombre()).isEqualTo("Entradas");
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).save(categoria);
        verify(categoriaMapper).toResponse(categoria);
    }

    @Test
    void updateCategoria_shouldThrowException_whenNotFound() {
        // Given
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> categoriaService.updateCategoria(99L, categoriaRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoría no encontrada");

        verify(categoriaRepository).findById(99L);
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void deleteCategoria_shouldDisableById() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // When
        categoriaService.deleteCategoria(1L);

        // Then
        assertThat(categoria.getDisponible()).isFalse();
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void deleteCategoria_shouldThrowException_whenNotFound() {
        // Given
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> categoriaService.deleteCategoria(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoría no encontrada");

        verify(categoriaRepository).findById(99L);
        verify(categoriaRepository, never()).save(any());
    }
}
