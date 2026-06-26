package com.nativa.reserva_service.controller;

import com.nativa.reserva_service.assemblers.ReservaModelAssembler;
import com.nativa.reserva_service.dto.ReservaRequest;
import com.nativa.reserva_service.dto.ReservaResponse;
import com.nativa.reserva_service.exception.ResourceNotFoundException;
import com.nativa.reserva_service.security.JwtUtil;
import com.nativa.reserva_service.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
@Import(ReservaModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservaControllerTest {

@Autowired
private MockMvc mockMvc;

@MockitoBean
private ReservaService reservaService;

@MockitoBean
private JwtUtil jwtUtil;

@Test
void getAllReservas_shouldReturnList() throws Exception {
        ReservaResponse reserva = ReservaResponse.builder()
                .id(1L).nombreCliente("Juan Pérez")
                .telefono("+56 9 1234 5678").email("juan@email.com")
                .fecha(LocalDate.of(2026, 7, 15)).hora(LocalTime.of(20, 0))
                .cantidadPersonas(4).estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.of(2026, 7, 14, 10, 0))
                .mesaId(1L).mesaNumero(1).mesaCapacidad(4).mesaUbicacion("Interior")
                .build();

        when(reservaService.getAllReservas()).thenReturn(List.of(reserva));

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservaResponseList").isArray())
                .andExpect(jsonPath("$._embedded.reservaResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.reservaResponseList[0].nombreCliente").value("Juan Pérez"))
                .andExpect(jsonPath("$._embedded.reservaResponseList[0].estado").value("PENDIENTE"));
}

@Test
void getAllReservas_shouldReturnEmptyList() throws Exception {
        when(reservaService.getAllReservas()).thenReturn(List.of());

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist());
}

@Test
void getReservaById_shouldReturnReserva() throws Exception {
        ReservaResponse reserva = ReservaResponse.builder()
                .id(1L).nombreCliente("Juan Pérez")
                .telefono("+56 9 1234 5678").email("juan@email.com")
                .fecha(LocalDate.of(2026, 7, 15)).hora(LocalTime.of(20, 0))
                .cantidadPersonas(4).estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.of(2026, 7, 14, 10, 0))
                .mesaId(1L).mesaNumero(1).mesaCapacidad(4).mesaUbicacion("Interior")
                .build();

        when(reservaService.getReservaById(1L)).thenReturn(reserva);

        mockMvc.perform(get("/reservas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCliente").value("Juan Pérez"))
                .andExpect(jsonPath("$._links.self").exists());
}

@Test
void getReservaById_shouldReturn404_whenNotFound() throws Exception {
        when(reservaService.getReservaById(99L)).thenThrow(new ResourceNotFoundException("Reserva no encontrada con id: 99"));

        mockMvc.perform(get("/reservas/99"))
                .andExpect(status().isNotFound());
}

@Test
void createReserva_shouldReturnReserva() throws Exception {
        ReservaResponse reserva = ReservaResponse.builder()
                .id(1L).nombreCliente("Juan Pérez")
                .telefono("+56 9 1234 5678").email("juan@email.com")
                .fecha(LocalDate.of(2026, 7, 15)).hora(LocalTime.of(20, 0))
                .cantidadPersonas(4).estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.of(2026, 7, 14, 10, 0))
                .mesaId(1L).mesaNumero(1).mesaCapacidad(4).mesaUbicacion("Interior")
                .build();

        when(reservaService.createReserva(any(ReservaRequest.class))).thenReturn(reserva);

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "nombreCliente": "Juan Pérez",
                                "telefono": "+56 9 1234 5678",
                                "email": "juan@email.com",
                                "fecha": "2026-07-15",
                                "hora": "20:00:00",
                                "cantidadPersonas": 4,
                                "mesaId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCliente").value("Juan Pérez"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
}

@Test
void updateReserva_shouldReturnUpdatedReserva() throws Exception {
        ReservaResponse reserva = ReservaResponse.builder()
                .id(1L).nombreCliente("Juan Pérez")
                .telefono("+56 9 1234 5678").email("juan@email.com")
                .fecha(LocalDate.of(2026, 7, 15)).hora(LocalTime.of(20, 0))
                .cantidadPersonas(4).estado("CONFIRMADA")
                .fechaCreacion(LocalDateTime.of(2026, 7, 14, 10, 0))
                .mesaId(1L).mesaNumero(1).mesaCapacidad(4).mesaUbicacion("Interior")
                .build();

        when(reservaService.updateReserva(eq(1L), any(ReservaRequest.class))).thenReturn(reserva);

        mockMvc.perform(put("/reservas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "nombreCliente": "Juan Pérez",
                                "telefono": "+56 9 1234 5678",
                                "email": "juan@email.com",
                                "fecha": "2026-07-15",
                                "hora": "20:00:00",
                                "cantidadPersonas": 4,
                                "mesaId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCliente").value("Juan Pérez"));
}

@Test
void cancelReserva_shouldReturnNoContent() throws Exception {
        doNothing().when(reservaService).cancelReserva(1L);

        mockMvc.perform(delete("/reservas/1"))
                .andExpect(status().isNoContent());

        verify(reservaService).cancelReserva(1L);
}
}
