package com.nativa.menu_service.controller;

import com.nativa.menu_service.assemblers.ProductoModelAssembler;
import com.nativa.menu_service.dto.ProductoRequest;
import com.nativa.menu_service.dto.ProductoResponse;
import com.nativa.menu_service.exception.ResourceNotFoundException;
import com.nativa.menu_service.security.JwtUtil;
import com.nativa.menu_service.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@Import(ProductoModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ProductoService productoService;

        @MockitoBean
        private JwtUtil jwtUtil;

        private ProductoResponse buildProducto() {
                return ProductoResponse.builder()
                        .id(1L).nombre("Empanada").descripcion("Empanada de pino")
                        .precio(BigDecimal.valueOf(2500)).disponible(true)
                        .categoriaId(1L).categoriaNombre("Entradas").build();
        }

        @Test
        void getProductosDisponibles_shouldReturnList() throws Exception {
        when(productoService.getAllDisponible()).thenReturn(List.of(buildProducto()));

        mockMvc.perform(get("/menu/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoResponseList").isArray())
                .andExpect(jsonPath("$._embedded.productoResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.productoResponseList[0].nombre").value("Empanada"));
}

        @Test
        void getProductosDisponibles_shouldReturnEmptyList() throws Exception {
        when(productoService.getAllDisponible()).thenReturn(List.of());

        mockMvc.perform(get("/menu/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist());
}

        @Test
        void getProductosByCategoria_shouldReturnList() throws Exception {
        when(productoService.agruparProductosDisponiblesPorCategoria("Entradas")).thenReturn(List.of(buildProducto()));

        mockMvc.perform(get("/menu/productos/categoria/Entradas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoResponseList[0].categoriaNombre").value("Entradas"));
}

        @Test
        void getAllProductos_shouldReturnList() throws Exception {
        when(productoService.getAllProductos()).thenReturn(List.of(buildProducto()));

        mockMvc.perform(get("/menu/productos/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoResponseList").isArray());
}

        @Test
        void getProductoById_shouldReturnProducto() throws Exception {
        when(productoService.getProductoById(1L)).thenReturn(buildProducto());

        mockMvc.perform(get("/menu/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Empanada"))
                .andExpect(jsonPath("$._links.self").exists());
}

        @Test
        void getProductoById_shouldReturn404_whenNotFound() throws Exception {
        when(productoService.getProductoById(99L)).thenThrow(new ResourceNotFoundException("Producto no encontrado"));

        mockMvc.perform(get("/menu/productos/99"))
                .andExpect(status().isNotFound());
}

        @Test
        void createProducto_shouldReturnProducto() throws Exception {
        when(productoService.createProducto(any(ProductoRequest.class))).thenReturn(buildProducto());

        mockMvc.perform(post("/menu/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "nombre": "Empanada",
                                "descripcion": "Empanada de pino",
                                "precio": 2500,
                                "categoriaId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Empanada"));
}

        @Test
        void updateProducto_shouldReturnUpdatedProducto() throws Exception {
        when(productoService.updateProducto(eq(1L), any(ProductoRequest.class))).thenReturn(buildProducto());

        mockMvc.perform(put("/menu/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "nombre": "Empanada",
                                "descripcion": "Empanada de pino",
                                "precio": 2500,
                                "categoriaId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Empanada"));
}

        @Test
        void deleteProducto_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/menu/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).deleteProducto(1L);
}
}
