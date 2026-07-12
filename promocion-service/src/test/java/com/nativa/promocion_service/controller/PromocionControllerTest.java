package com.nativa.promocion_service.controller;

import com.nativa.promocion_service.dto.PromocionRequest;
import com.nativa.promocion_service.dto.PromocionResponse;
import com.nativa.promocion_service.exception.ResourceNotFoundException;
import com.nativa.promocion_service.security.JwtUtil;
import com.nativa.promocion_service.service.PromocionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
@AutoConfigureMockMvc(addFilters = false)
class PromocionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private PromocionService promocionService;

        @MockitoBean
        private JwtUtil jwtUtil;

        private PromocionResponse buildPromocion() {
                return PromocionResponse.builder()
                        .id(1L)
                        .codigo("CKD34L")
                        .descripcion("Descuento solo valido por hoy")
                        .porcentajeDescuento(BigDecimal.valueOf(25))
                        .fechaInicio(LocalDate.of(2026, 8, 1))
                        .fechaFin(LocalDate.of(2026, 9, 1))
                        .activo(true)
                        .build();
        }

        @Test
        void getAllPromociones_shouldReturnList() throws Exception {
        when(promocionService.getAllPromociones()).thenReturn(List.of(buildPromocion()));

        mockMvc.perform(get("/promocion/promociones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].codigo").value("CKD34L"));
}

        @Test
        void getAllPromociones_shouldReturnEmptyList() throws Exception {
        when(promocionService.getAllPromociones()).thenReturn(List.of());

        mockMvc.perform(get("/promocion/promociones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
}

        @Test
        void getPromocionById_shouldReturnPromocion() throws Exception {
        when(promocionService.getPromocionById(1L)).thenReturn(buildPromocion());

        mockMvc.perform(get("/promocion/promociones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descripcion").value("Descuento solo valido por hoy"));
}

        @Test
        void getPromocionById_shouldReturn404_whenNotFound() throws Exception {
        when(promocionService.getPromocionById(99L)).thenThrow(new ResourceNotFoundException("Promocion no encontrada"));

        mockMvc.perform(get("/promocion/promociones/99"))
                .andExpect(status().isNotFound());
}

        @Test
        void createPromocion_shouldReturnPromocion() throws Exception {
        when(promocionService.createPromocion(any(PromocionRequest.class))).thenReturn(buildPromocion());

        mockMvc.perform(post("/promocion/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "codigo": "CKD34L",
                                "descripcion": "Descuento solo valido por hoy",
                                "porcentajeDescuento": 25,
                                "fechaInicio": "2026-08-01",
                                "fechaFin": "2026-09-01",
                                "activo": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("CKD34L"));
}

        @Test
        void deletePromocion_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/promocion/promociones/1"))
                .andExpect(status().isNoContent());

        verify(promocionService).deletePromocion(1L);
}
}
