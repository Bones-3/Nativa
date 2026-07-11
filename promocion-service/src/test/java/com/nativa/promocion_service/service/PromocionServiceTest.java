package com.nativa.promocion_service.service;

import com.nativa.promocion_service.dto.PromocionRequest;
import com.nativa.promocion_service.dto.PromocionResponse;
import com.nativa.promocion_service.exception.ResourceNotFoundException;
import com.nativa.promocion_service.mapper.PromocionMapper;
import com.nativa.promocion_service.model.Promocion;
import com.nativa.promocion_service.repository.PromocionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromocionServiceTest {

    @Mock
    private PromocionRepository promocionRepository;

    @Mock
    private PromocionMapper promocionMapper;

    @InjectMocks
    private PromocionService promocionService;

    private Promocion promocion;
    private PromocionResponse promocionResponse;
    private PromocionRequest promocionRequest;

    @BeforeEach
    void setUp() {
        promocion = new Promocion(1L,"CKD34L","Descuento solo valido por hoy", BigDecimal.valueOf(25), LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 11), true );
        promocionResponse = PromocionResponse.builder()
                .id(1L)
                .codigo("CKD34L")
                .descripcion("Descuento solo valido por hoy")
                .porcentajeDescuento(BigDecimal.valueOf(25))
                .fechaInicio(LocalDate.of(2026, 7, 10))
                .fechaFin(LocalDate.of(2026, 7, 11))
                .activo(true)
                .build();
        promocionRequest = new PromocionRequest();
        promocionRequest.setDescripcion("Descuento solo por hoy");
        promocionRequest.setPorcentajeDescuento(BigDecimal.valueOf(25));
        promocionRequest.setFechaInicio(LocalDate.of(2026, 7, 10));
        promocionRequest.setFechaFin(LocalDate.of(2026, 7, 11));
        promocionRequest.setActivo(true);
    }

    @Test
    void getAllPromociones_shouldReturnList() {
        when(promocionRepository.findAll()).thenReturn(List.of(promocion));
        when(promocionMapper.toResponse(promocion)).thenReturn(promocionResponse);

        List<PromocionResponse> result = promocionService.getAllPromociones();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getCodigo()).isEqualTo("CKD34L");
        assertThat(result.getFirst().getDescripcion()).isEqualTo("Descuento solo valido por hoy");
        assertThat(result.getFirst().getPorcentajeDescuento()).isEqualTo((BigDecimal.valueOf(25)));
        assertThat(result.getFirst().getFechaInicio()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(result.getFirst().getFechaFin()).isEqualTo(LocalDate.of(2026, 7, 11));
        assertThat(result.getFirst().getActivo()).isEqualTo(true);
        verify(promocionRepository).findAll();
        verify(promocionMapper).toResponse(promocion);
    }

    @Test
    void getAllPromocion_shouldReturnEmptyList_whenNoPromocion() {
        when(promocionRepository.findAll()).thenReturn(List.of());

        List<PromocionResponse> result = promocionService.getAllPromociones();

        assertThat(result).isEmpty();
        verify(promocionRepository).findAll();
        verifyNoInteractions(promocionMapper);
    }

    @Test
    void getPromocionById_shouldReturnPromocion() {
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));
        when(promocionMapper.toResponse(promocion)).thenReturn(promocionResponse);

        PromocionResponse result = promocionService.getPromocionById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCodigo()).isEqualTo("CKD34L");
        assertThat(result.getDescripcion()).isEqualTo("Descuento solo valido por hoy");
        assertThat(result.getPorcentajeDescuento()).isEqualTo((BigDecimal.valueOf(25)));
        assertThat(result.getFechaInicio()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(result.getFechaFin()).isEqualTo(LocalDate.of(2026, 7, 11));
        assertThat(result.getActivo()).isEqualTo(true);
        verify(promocionRepository).findById(1L);
        verify(promocionMapper).toResponse(promocion);
    }

    @Test
    void getPromocionById_shouldThrowException_whenNotFound() {
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> promocionService.getPromocionById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Promocion no encontrada");

        verify(promocionRepository).findById(99L);
        verifyNoInteractions(promocionMapper);
    }

    @Test
    void createResena_shouldReturnSavedPromocion() {
        when(promocionMapper.toEntity(promocionRequest)).thenReturn(promocion);
        when(promocionRepository.save(promocion)).thenReturn(promocion);
        when(promocionMapper.toResponse(promocion)).thenReturn(promocionResponse);

        PromocionResponse result = promocionService.createPromocion(promocionRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCodigo()).isEqualTo("CKD34L");
        assertThat(result.getDescripcion()).isEqualTo("Descuento solo valido por hoy");
        assertThat(result.getPorcentajeDescuento()).isEqualTo((BigDecimal.valueOf(25)));
        assertThat(result.getFechaInicio()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(result.getFechaFin()).isEqualTo(LocalDate.of(2026, 7, 11));
        assertThat(result.getActivo()).isEqualTo(true);
        verify(promocionRepository).save(promocion);
        verify(promocionMapper).toEntity(promocionRequest);
        verify(promocionMapper).toResponse(promocion);
    }

    @Test
    void deletePromocion_shouldDeleteById() {
        promocionService.deletePromocion(1L);

        verify(promocionRepository).deleteById(1L);
    }
}
