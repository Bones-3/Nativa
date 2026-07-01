package com.example.inventario.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.inventario.assemblers.InventarioModelAssembler;
import com.example.inventario.dto.InventarioRequest;
import com.example.inventario.dto.InventarioResponse;
import com.example.inventario.exception.NotFoundException;
import com.example.inventario.security.JwtFilter;
import com.example.inventario.service.InventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(InventarioController.class)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventarioService inventarioService;

    @MockitoBean
    private InventarioModelAssembler assembler;

    @MockitoBean(name = "inventarioJwtFilter")
    private JwtFilter jwtFilter;

    private InventarioResponse buildResponse() {
        return new InventarioResponse(1L, 10L, "Arroz", 50, 10, "kg");
    }

    private InventarioRequest buildRequest() {
        return new InventarioRequest(10L, "Arroz", 50, 10, "kg");
    }

    @Test
    @WithMockUser
    void obtenerTodos_shouldReturn200WithList() throws Exception {
        // Given
        InventarioResponse response = buildResponse();
        when(inventarioService.obtenerTodos()).thenReturn(List.of(response));
        when(assembler.toModel(any(InventarioResponse.class)))
                .thenReturn(EntityModel.of(response));

        // When / Then
        mockMvc.perform(get("/inventario")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk());

        verify(inventarioService).obtenerTodos();
    }

    @Test
    @WithMockUser
    void obtenerPorId_shouldReturn200WithEntityModel() throws Exception {
        // Given
        InventarioResponse response = buildResponse();
        when(inventarioService.obtenerPorId(1L)).thenReturn(response);
        when(assembler.toModel(response)).thenReturn(EntityModel.of(response));

        // When / Then
        mockMvc.perform(get("/inventario/1")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreProducto").value("Arroz"));

        verify(inventarioService).obtenerPorId(1L);
    }

    @Test
    @WithMockUser
    void obtenerPorId_shouldReturn404_whenNotFound() throws Exception {
        // Given
        when(inventarioService.obtenerPorId(99L))
                .thenThrow(new NotFoundException("Inventario no encontrado con id: 99"));

        // When / Then
        mockMvc.perform(get("/inventario/99")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isNotFound());

        verify(inventarioService).obtenerPorId(99L);
    }

    @Test
    @WithMockUser
    void crear_shouldReturn201() throws Exception {
        // Given
        InventarioRequest request = buildRequest();
        InventarioResponse response = buildResponse();
        when(inventarioService.crearInventario(any(InventarioRequest.class))).thenReturn(response);

        // When / Then
        mockMvc.perform(post("/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreProducto").value("Arroz"));

        verify(inventarioService).crearInventario(any(InventarioRequest.class));
    }

    @Test
    @WithMockUser
    void actualizar_shouldReturn200() throws Exception {
        // Given
        InventarioRequest request = buildRequest();
        InventarioResponse response = buildResponse();
        when(inventarioService.actualizar(eq(1L), any(InventarioRequest.class))).thenReturn(response);

        // When / Then
        mockMvc.perform(put("/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(inventarioService).actualizar(eq(1L), any(InventarioRequest.class));
    }

    @Test
    @WithMockUser
    void eliminar_shouldReturn204() throws Exception {
        // Given
        doNothing().when(inventarioService).eliminar(1L);

        // When / Then
        mockMvc.perform(delete("/inventario/1"))
                .andExpect(status().isNoContent());

        verify(inventarioService).eliminar(1L);
    }

    @Test
    @WithMockUser
    void obtenerStockBajo_shouldReturn200WithList() throws Exception {
        // Given
        InventarioResponse response = buildResponse();
        response.setStockActual(5);
        when(inventarioService.obtenerStockBajo()).thenReturn(List.of(response));
        when(assembler.toModel(any(InventarioResponse.class)))
                .thenReturn(EntityModel.of(response));

        // When / Then
        mockMvc.perform(get("/inventario/stock/bajo")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk());

        verify(inventarioService).obtenerStockBajo();
    }
}
