package com.nativa.menu_service.service;

import com.nativa.menu_service.dto.ProductoRequest;
import com.nativa.menu_service.dto.ProductoResponse;
import com.nativa.menu_service.exception.ResourceNotFoundException;
import com.nativa.menu_service.mapper.ProductoMapper;
import com.nativa.menu_service.model.Categoria;
import com.nativa.menu_service.model.Producto;
import com.nativa.menu_service.repository.CategoriaRepository;
import com.nativa.menu_service.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    private Categoria categoria;
    private Producto producto;
    private ProductoResponse productoResponse;
    private ProductoRequest productoRequest;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Entradas", true);
        producto = new Producto(1L, "Empanada", "Empanada de pino", BigDecimal.valueOf(2500), true, categoria);
        productoResponse = ProductoResponse.builder()
                .id(1L).nombre("Empanada").descripcion("Empanada de pino")
                .precio(BigDecimal.valueOf(2500)).disponible(true)
                .categoriaId(1L).categoriaNombre("Entradas").build();
        productoRequest = new ProductoRequest();
        productoRequest.setNombre("Empanada");
        productoRequest.setDescripcion("Empanada de pino");
        productoRequest.setPrecio(BigDecimal.valueOf(2500));
        productoRequest.setCategoriaId(1L);
    }

    @Test
    void getAllDisponible_shouldReturnList() {
        // Given
        when(productoRepository.findByDisponibleTrue()).thenReturn(List.of(producto));
        when(productoMapper.toResponse(producto)).thenReturn(productoResponse);

        // When
        List<ProductoResponse> result = productoService.getAllDisponible();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNombre()).isEqualTo("Empanada");
        verify(productoRepository).findByDisponibleTrue();
        verify(productoMapper).toResponse(producto);
    }

    @Test
    void getAllDisponible_shouldReturnEmptyList_whenNoProductos() {
        // Given
        when(productoRepository.findByDisponibleTrue()).thenReturn(List.of());

        // When
        List<ProductoResponse> result = productoService.getAllDisponible();

        // Then
        assertThat(result).isEmpty();
        verify(productoRepository).findByDisponibleTrue();
        verifyNoInteractions(productoMapper);
    }

    @Test
    void getAllProductos_shouldReturnList() {
        // Given
        when(productoRepository.findAll()).thenReturn(List.of(producto));
        when(productoMapper.toResponse(producto)).thenReturn(productoResponse);

        // When
        List<ProductoResponse> result = productoService.getAllProductos();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNombre()).isEqualTo("Empanada");
        verify(productoRepository).findAll();
        verify(productoMapper).toResponse(producto);
    }

    @Test
    void agruparProductosDisponiblesPorCategoria_shouldReturnList() {
        // Given
        when(productoRepository.findByDisponibleTrueAndCategoria_NombreIgnoreCase("Entradas")).thenReturn(List.of(producto));
        when(productoMapper.toResponse(producto)).thenReturn(productoResponse);

        // When
        List<ProductoResponse> result = productoService.agruparProductosDisponiblesPorCategoria("Entradas");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCategoriaNombre()).isEqualTo("Entradas");
        verify(productoRepository).findByDisponibleTrueAndCategoria_NombreIgnoreCase("Entradas");
        verify(productoMapper).toResponse(producto);
    }

    @Test
    void getProductoById_shouldReturnProducto() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoMapper.toResponse(producto)).thenReturn(productoResponse);

        // When
        ProductoResponse result = productoService.getProductoById(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Empanada");
        verify(productoRepository).findById(1L);
        verify(productoMapper).toResponse(producto);
    }

    @Test
    void getProductoById_shouldThrowException_whenNotFound() {
        // Given
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productoService.getProductoById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Producto no encontrado");

        verify(productoRepository).findById(99L);
        verifyNoInteractions(productoMapper);
    }

    @Test
    void createProducto_shouldReturnSavedProducto() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoMapper.toEntity(productoRequest)).thenReturn(producto);
        when(productoRepository.save(producto)).thenReturn(producto);
        when(productoMapper.toResponse(producto)).thenReturn(productoResponse);

        // When
        ProductoResponse result = productoService.createProducto(productoRequest);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Empanada");
        verify(categoriaRepository).findById(1L);
        verify(productoRepository).save(producto);
        verify(productoMapper).toEntity(productoRequest);
        verify(productoMapper).toResponse(producto);
    }

    @Test
    void createProducto_shouldThrowException_whenCategoriaNotFound() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productoService.createProducto(productoRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoría no encontrada");

        verify(categoriaRepository).findById(1L);
        verifyNoInteractions(productoMapper);
        verify(productoRepository, never()).save(any());
    }

    @Test
    void updateProducto_shouldReturnUpdatedProducto() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(productoMapper.toResponse(producto)).thenReturn(productoResponse);

        // When
        ProductoResponse result = productoService.updateProducto(1L, productoRequest);

        // Then
        assertThat(result.getNombre()).isEqualTo("Empanada");
        verify(productoRepository).findById(1L);
        verify(categoriaRepository).findById(1L);
        verify(productoRepository).save(producto);
        verify(productoMapper).toResponse(producto);
    }

    @Test
    void updateProducto_shouldThrowException_whenProductoNotFound() {
        // Given
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productoService.updateProducto(99L, productoRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Producto no encontrado");

        verify(productoRepository).findById(99L);
        verify(productoRepository, never()).save(any());
    }

    @Test
    void deleteProducto_shouldDisableById() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        productoService.deleteProducto(1L);

        // Then
        assertThat(producto.getDisponible()).isFalse();
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(producto);
    }

    @Test
    void deleteProducto_shouldThrowException_whenNotFound() {
        // Given
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productoService.deleteProducto(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Producto no encontrado");

        verify(productoRepository).findById(99L);
        verify(productoRepository, never()).save(any());
    }
}
