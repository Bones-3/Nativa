package com.nativa.promocion_service.controller;

import com.nativa.promocion_service.assemblers.PromocionModelAssembler;
import com.nativa.promocion_service.dto.PromocionRequest;
import com.nativa.promocion_service.dto.PromocionResponse;
import com.nativa.promocion_service.exception.ResourceNotFoundException;
import com.nativa.promocion_service.security.JwtUtil;
import com.nativa.promocion_service.service.PromocionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromocionController.class)
@Import(PromocionModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class PromocionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private PromocionService promocionService;

        @MockitoBean
        private JwtUtil jwtUtil;

        @Test
        void getAllPromociones_shouldReturnList() throws Exception {
        PromocionResponse promocion = PromocionResponse.builder()
                .id(1L)
                .codigo("CKD34L")
                .descripcion("Descuento solo valido por hoy")
                .porcentajeDescuento(BigDecimal.valueOf(25))
                .fechaInicio(LocalDate.of(2026, 7, 10))
                .fechaFin(LocalDate.of(2026, 7, 11))
                .activo(true)
                .build();

        when(promocionService.getAllPromociones()).thenReturn(List.of(promocion));

        mockMvc.perform(get("/promocion/promociones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.promocionResponseList").isArray())
                .andExpect(jsonPath("$._embedded.promocionResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.promocionResponseList[0].comentario").value("Descuento solo valido por hoy"));
        }       

        @Test
        void getAllPromociones_shouldReturnEmptyList() throws Exception {
        when(promocionService.getAllPromociones()).thenReturn(List.of());

        mockMvc.perform(get("/promocion/promociones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist());
        }

        @Test
        void getPromocionById_shouldReturnPromocion() throws Exception {
        PromocionResponse promocion = PromocionResponse.builder()
                .id(1L)
                .codigo("CKD34L")
                .descripcion("Descuento solo valido por hoy")
                .porcentajeDescuento(BigDecimal.valueOf(25))
                .fechaInicio(LocalDate.of(2026, 7, 10))
                .fechaFin(LocalDate.of(2026, 7, 11))
                .activo(true)
                .build();

        when(promocionService.getPromocionById(1L)).thenReturn(promocion);

        mockMvc.perform(get("/promocion/promociones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comentario").value("Descuento solo valido por hoy"))
                .andExpect(jsonPath("$._links.self").exists());
        }

        @Test
        void getPromocionById_shouldReturn404_whenNotFound() throws Exception {
        when(promocionService.getPromocionById(99L)).thenThrow(new ResourceNotFoundException("Rese\u00f1a no encontrada"));

        mockMvc.perform(get("/promocion/promociones/99"))
                .andExpect(status().isNotFound());
        }

        @Test
        void createPromocion_shouldReturnPromocion() throws Exception {
        PromocionResponse promocion = PromocionResponse.builder()
                .id(1L)
                .codigo("CKD34L")
                .descripcion("Descuento solo valido por hoy")
                .porcentajeDescuento(BigDecimal.valueOf(25))
                .fechaInicio(LocalDate.of(2026, 7, 10))
                .fechaFin(LocalDate.of(2026, 7, 11))
                .activo(true)
                .build();

        when(promocionService.createPromocion(any(PromocionRequest.class))).thenReturn(promocion);

        mockMvc.perform(post("/promocion/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "productoId": 1,
                                "usuarioId": 1,
                                "comentario": "Buen producto",
                                "calificacion": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comentario").value("Buen producto"));
        }

        @Test
        void deletePromocion_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/promocion/promociones/1"))
                .andExpect(status().isNoContent());

        verify(promocionService).deletePromocion(any());
        }
}
