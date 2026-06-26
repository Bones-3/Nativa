package com.nativa.reserva_service.controller;

import com.nativa.reserva_service.assemblers.MesaModelAssembler;
import com.nativa.reserva_service.dto.MesaRequest;
import com.nativa.reserva_service.dto.MesaResponse;
import com.nativa.reserva_service.exception.ResourceNotFoundException;
import com.nativa.reserva_service.security.JwtUtil;
import com.nativa.reserva_service.service.MesaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MesaController.class)
@Import(MesaModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class MesaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private MesaService mesaService;

        @MockitoBean
        private JwtUtil jwtUtil;

        @Test
        void getAllMesas_shouldReturnList() throws Exception {
        MesaResponse mesa = MesaResponse.builder()
                .id(1L).numero(1).capacidad(4)
                .ubicacion("Interior").disponible(true)
                .build();

        when(mesaService.getAllMesas()).thenReturn(List.of(mesa));

        mockMvc.perform(get("/mesas/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.mesaResponseList").isArray())
                .andExpect(jsonPath("$._embedded.mesaResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.mesaResponseList[0].numero").value(1))
                .andExpect(jsonPath("$._embedded.mesaResponseList[0].ubicacion").value("Interior"));
}

        @Test
        void getMesasDisponibles_shouldReturnList() throws Exception {
        MesaResponse mesa = MesaResponse.builder()
                .id(1L).numero(1).capacidad(4)
                .ubicacion("Interior").disponible(true)
                .build();

        when(mesaService.getMesasDisponibles()).thenReturn(List.of(mesa));

        mockMvc.perform(get("/mesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.mesaResponseList").isArray())
                .andExpect(jsonPath("$._embedded.mesaResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.mesaResponseList[0].disponible").value(true));
}

        @Test
        void getMesaById_shouldReturnMesa() throws Exception {
        MesaResponse mesa = MesaResponse.builder()
                .id(1L).numero(1).capacidad(4)
                .ubicacion("Interior").disponible(true)
                .build();

        when(mesaService.getMesaById(1L)).thenReturn(mesa);

        mockMvc.perform(get("/mesas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numero").value(1))
                .andExpect(jsonPath("$._links.self").exists());
}

        @Test
        void getMesaById_shouldReturn404_whenNotFound() throws Exception {
        when(mesaService.getMesaById(99L)).thenThrow(new ResourceNotFoundException("Mesa no encontrada con id: 99"));

        mockMvc.perform(get("/mesas/99"))
                .andExpect(status().isNotFound());
}

        @Test
        void createMesa_shouldReturnMesa() throws Exception {
        MesaResponse mesa = MesaResponse.builder()
                .id(1L).numero(1).capacidad(4)
                .ubicacion("Interior").disponible(true)
                .build();

        when(mesaService.createMesa(any(MesaRequest.class))).thenReturn(mesa);

        mockMvc.perform(post("/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "numero": 1,
                                "capacidad": 4,
                                "ubicacion": "Interior"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numero").value(1))
                .andExpect(jsonPath("$.ubicacion").value("Interior"));
}

        @Test
        void updateMesa_shouldReturnUpdatedMesa() throws Exception {
        MesaResponse mesa = MesaResponse.builder()
                .id(1L).numero(2).capacidad(6)
                .ubicacion("Terraza").disponible(true)
                .build();

        when(mesaService.updateMesa(eq(1L), any(MesaRequest.class))).thenReturn(mesa);

        mockMvc.perform(put("/mesas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "numero": 2,
                                "capacidad": 6,
                                "ubicacion": "Terraza"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numero").value(2))
                .andExpect(jsonPath("$.ubicacion").value("Terraza"));
}

        @Test
        void deleteMesa_shouldReturnNoContent() throws Exception {
        doNothing().when(mesaService).deleteMesa(1L);

        mockMvc.perform(delete("/mesas/1"))
                .andExpect(status().isNoContent());

        verify(mesaService).deleteMesa(1L);
}
}
