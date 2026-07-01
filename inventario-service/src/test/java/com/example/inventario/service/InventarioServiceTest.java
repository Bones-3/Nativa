package com.example.inventario.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.inventario.dto.InventarioRequest;
import com.example.inventario.dto.InventarioResponse;
import com.example.inventario.exception.NotFoundException;
import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Inventario buildInventario() {
        Inventario inv = new Inventario();
        inv.setId(1L);
        inv.setProductoId(10L);
        inv.setNombreProducto("Arroz");
        inv.setStockActual(50);
        inv.setStockMinimo(10);
        inv.setUnidadMedida("kg");
        inv.setUltimaActualizacion(LocalDateTime.now());
        return inv;
    }

    @Test
    void obtenerTodos_shouldReturnList() {
        // Given
        Inventario inv = buildInventario();
        when(inventarioRepository.findAll()).thenReturn(List.of(inv));

        // When
        List<InventarioResponse> result = inventarioService.obtenerTodos();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductoId()).isEqualTo(10L);
        assertThat(result.get(0).getNombreProducto()).isEqualTo("Arroz");
        verify(inventarioRepository).findAll();
    }

    @Test
    void obtenerTodos_shouldReturnEmptyList() {
        // Given
        when(inventarioRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<InventarioResponse> result = inventarioService.obtenerTodos();

        // Then
        assertThat(result).isEmpty();
        verify(inventarioRepository).findAll();
    }

    @Test
    void obtenerPorId_shouldReturnInventario() {
        // Given
        Inventario inv = buildInventario();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv));

        // When
        InventarioResponse result = inventarioService.obtenerPorId(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombreProducto()).isEqualTo("Arroz");
        assertThat(result.getStockActual()).isEqualTo(50);
        verify(inventarioRepository).findById(1L);
    }

    @Test
    void obtenerPorId_shouldThrowException_whenNotFound() {
        // Given
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> inventarioService.obtenerPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
        verify(inventarioRepository).findById(99L);
    }

    @Test
    void crearInventario_shouldReturnSavedInventario() {
        // Given
        InventarioRequest request = new InventarioRequest(10L, "Arroz", 50, 10, "kg");
        Inventario guardado = buildInventario();
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(guardado);

        // When
        InventarioResponse result = inventarioService.crearInventario(request);

        // Then
        assertThat(result.getProductoId()).isEqualTo(10L);
        assertThat(result.getNombreProducto()).isEqualTo("Arroz");
        assertThat(result.getStockActual()).isEqualTo(50);
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void eliminar_shouldDeleteById() {
        // Given
        when(inventarioRepository.existsById(1L)).thenReturn(true);

        // When
        inventarioService.eliminar(1L);

        // Then
        verify(inventarioRepository).existsById(1L);
        verify(inventarioRepository).deleteById(1L);
    }

    @Test
    void eliminar_shouldThrowException_whenNotFound() {
        // Given
        when(inventarioRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> inventarioService.eliminar(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
        verify(inventarioRepository).existsById(99L);
        verify(inventarioRepository, never()).deleteById(any());
    }

    @Test
    void obtenerStockBajo_shouldReturnList() {
        // Given
        Inventario inv = buildInventario();
        inv.setStockActual(5);
        when(inventarioRepository.findStockBajo()).thenReturn(List.of(inv));

        // When
        List<InventarioResponse> result = inventarioService.obtenerStockBajo();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockActual()).isEqualTo(5);
        verify(inventarioRepository).findStockBajo();
    }
}
