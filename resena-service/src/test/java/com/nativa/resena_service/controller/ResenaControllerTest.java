package com.nativa.resena_service.controller;

import com.nativa.resena_service.assemblers.ResenaModelAssembler;
import com.nativa.resena_service.dto.ResenaRequest;
import com.nativa.resena_service.dto.ResenaResponse;
import com.nativa.resena_service.exception.ResourceNotFoundException;
import com.nativa.resena_service.service.ResenaService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResenaController.class)
@Import(ResenaModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class ResenaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ResenaService resenaService;

        @Test
        void getAllResenas_shouldReturnList() throws Exception {
        ResenaResponse resena = ResenaResponse.builder()
                .id(1L).productoId(1L).usuarioId(1L)
                .comentario("Buen producto").calificacion(5).build();

        when(resenaService.getAllResenas()).thenReturn(List.of(resena));

        mockMvc.perform(get("/resena/resenas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.resenaResponseList").isArray())
                .andExpect(jsonPath("$._embedded.resenaResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.resenaResponseList[0].comentario").value("Buen producto"));
}

        @Test
        void getAllResenas_shouldReturnEmptyList() throws Exception {
        when(resenaService.getAllResenas()).thenReturn(List.of());

        mockMvc.perform(get("/resena/resenas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist());
}

        @Test
        void getResenaById_shouldReturnResena() throws Exception {
        ResenaResponse resena = ResenaResponse.builder()
                .id(1L).productoId(1L).usuarioId(1L)
                .comentario("Buen producto").calificacion(5).build();

        when(resenaService.getResenaById(1L)).thenReturn(resena);

        mockMvc.perform(get("/resena/resenas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comentario").value("Buen producto"))
                .andExpect(jsonPath("$._links.self").exists());
}

        @Test
        void getResenaById_shouldReturn404_whenNotFound() throws Exception {
        when(resenaService.getResenaById(99L)).thenThrow(new ResourceNotFoundException("Rese\u00f1a no encontrada"));

        mockMvc.perform(get("/resena/resenas/99"))
                .andExpect(status().isNotFound());
}

        @Test
        void createResena_shouldReturnResena() throws Exception {
        ResenaResponse resena = ResenaResponse.builder()
                .id(1L).productoId(1L).usuarioId(1L)
                .comentario("Buen producto").calificacion(5).build();

        when(resenaService.createResena(any(ResenaRequest.class))).thenReturn(resena);

        mockMvc.perform(post("/resena/resenas")
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
        void resenaPedido_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/resena/resenas"))
                .andExpect(status().isNoContent());

        verify(resenaService).resenaPedido(any());
}
}
