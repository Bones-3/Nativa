package com.nativa.menu_service.controller;

import com.nativa.menu_service.assemblers.CategoriaModelAssembler;
import com.nativa.menu_service.dto.CategoriaRequest;
import com.nativa.menu_service.dto.CategoriaResponse;
import com.nativa.menu_service.exception.ResourceNotFoundException;
import com.nativa.menu_service.security.JwtUtil;
import com.nativa.menu_service.service.CategoriaService;
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

@WebMvcTest(CategoriaController.class)
@Import(CategoriaModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoriaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CategoriaService categoriaService;

        @MockitoBean
        private JwtUtil jwtUtil;

        @Test
        void getCategoriasDisponible_shouldReturnList() throws Exception {
        CategoriaResponse categoria = CategoriaResponse.builder()
                .id(1L).nombre("Entradas").disponible(true).build();

        when(categoriaService.getCategoriasDisponible()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/menu/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.categoriaResponseList").isArray())
                .andExpect(jsonPath("$._embedded.categoriaResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.categoriaResponseList[0].nombre").value("Entradas"));
}

        @Test
        void getCategoriasDisponible_shouldReturnEmptyList() throws Exception {
        when(categoriaService.getCategoriasDisponible()).thenReturn(List.of());

        mockMvc.perform(get("/menu/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist());
}

        @Test
        void getAllCategorias_shouldReturnList() throws Exception {
        CategoriaResponse categoria = CategoriaResponse.builder()
                .id(1L).nombre("Entradas").disponible(true).build();

        when(categoriaService.getAllCategorias()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/menu/categorias/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.categoriaResponseList").isArray())
                .andExpect(jsonPath("$._embedded.categoriaResponseList[0].nombre").value("Entradas"));
}

        @Test
        void getCategoriaById_shouldReturnCategoria() throws Exception {
        CategoriaResponse categoria = CategoriaResponse.builder()
                .id(1L).nombre("Entradas").disponible(true).build();

        when(categoriaService.getCategoriaById(1L)).thenReturn(categoria);

        mockMvc.perform(get("/menu/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Entradas"))
                .andExpect(jsonPath("$._links.self").exists());
}

        @Test
        void getCategoriaById_shouldReturn404_whenNotFound() throws Exception {
        when(categoriaService.getCategoriaById(99L)).thenThrow(new ResourceNotFoundException("Categoría no encontrada"));

        mockMvc.perform(get("/menu/categorias/99"))
                .andExpect(status().isNotFound());
}

        @Test
        void createCategoria_shouldReturnCategoria() throws Exception {
        CategoriaResponse categoria = CategoriaResponse.builder()
                .id(1L).nombre("Entradas").disponible(true).build();

        when(categoriaService.createCategoria(any(CategoriaRequest.class))).thenReturn(categoria);

        mockMvc.perform(post("/menu/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "nombre": "Entradas"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Entradas"));
}

        @Test
        void updateCategoria_shouldReturnUpdatedCategoria() throws Exception {
        CategoriaResponse categoria = CategoriaResponse.builder()
                .id(1L).nombre("Postres").disponible(true).build();

        when(categoriaService.updateCategoria(eq(1L), any(CategoriaRequest.class))).thenReturn(categoria);

        mockMvc.perform(put("/menu/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "nombre": "Postres"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Postres"));
}

        @Test
        void deleteCategoria_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/menu/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService).deleteCategoria(1L);
}
}
