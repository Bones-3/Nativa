package com.nativa.horario_service.controller;

import com.nativa.horario_service.service.HorarioService;
import com.nativa.horario_service.dto.HorarioRequest;
import com.nativa.horario_service.dto.HorarioResponse;
import com.nativa.horario_service.exception.ResourceNotFoundException;
import com.nativa.horario_service.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HorarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class HorarioControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private HorarioService horarioService;

        @MockitoBean
        private JwtUtil jwtUtil;

        private HorarioResponse buildHorario() {
                return HorarioResponse.builder()
                        .id(1L)
                        .diaSemana("Lunes, Martes, Miercoles, Jueves, Viernes")
                        .horaApertura(LocalTime.of(9, 0))
                        .horaCierre(LocalTime.of(22, 0))
                        .cerrado(false)
                        .build();
        }

        @Test
        void getAllHorarios_shouldReturnList() throws Exception {
        when(horarioService.getAllHorarios()).thenReturn(List.of(buildHorario()));

        mockMvc.perform(get("/horario/horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].diaSemana").value("Lunes, Martes, Miercoles, Jueves, Viernes"));
}

        @Test
        void getAllHorarios_shouldReturnEmptyList() throws Exception {
        when(horarioService.getAllHorarios()).thenReturn(List.of());

        mockMvc.perform(get("/horario/horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
}

        @Test
        void getHorarioById_shouldReturnHorario() throws Exception {
        when(horarioService.getHorarioById(1L)).thenReturn(buildHorario());

        mockMvc.perform(get("/horario/horarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diaSemana").value("Lunes, Martes, Miercoles, Jueves, Viernes"));
}

        @Test
        void getHorarioById_shouldReturn404_whenNotFound() throws Exception {
        when(horarioService.getHorarioById(99L)).thenThrow(new ResourceNotFoundException("Horario no encontrado"));

        mockMvc.perform(get("/horario/horarios/99"))
                .andExpect(status().isNotFound());
}

        @Test
        void createHorario_shouldReturnHorario() throws Exception {
        when(horarioService.createHorario(any(HorarioRequest.class))).thenReturn(buildHorario());

        mockMvc.perform(post("/horario/horarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "diaSemana": "Lunes, Martes, Miercoles, Jueves, Viernes",
                                "horaApertura": "09:00",
                                "horaCierre": "22:00",
                                "cerrado": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diaSemana").value("Lunes, Martes, Miercoles, Jueves, Viernes"));
}

        @Test
        void deleteHorario_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/horario/horarios/1"))
                .andExpect(status().isNoContent());

        verify(horarioService).deleteHorario(any());
}
}
