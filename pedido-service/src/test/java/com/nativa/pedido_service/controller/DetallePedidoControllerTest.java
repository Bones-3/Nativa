package com.nativa.pedido_service.controller;

import com.nativa.pedido_service.assemblers.DetallePedidoModelAssembler;
import com.nativa.pedido_service.dto.DetallePedidoResponse;
import com.nativa.pedido_service.exception.ResourceNotFoundException;
import com.nativa.pedido_service.security.JwtUtil;
import com.nativa.pedido_service.service.DetallePedidoService;

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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DetallePedidoController.class)
@Import(DetallePedidoModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class DetallePedidoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private DetallePedidoService detallePedidoService;

        @MockitoBean
        private JwtUtil jwtUtil;

        @Test
        void getAll_shouldReturnList() throws Exception {
        DetallePedidoResponse d1 = DetallePedidoResponse.builder()
                .id(1L).productoId(10L).pedidoId(1L).cantidad(2)
                .precioUnitario(BigDecimal.valueOf(5200))
                .subtotal(BigDecimal.valueOf(10400))
                .build();
        DetallePedidoResponse d2 = DetallePedidoResponse.builder()
                .id(2L).productoId(5L).pedidoId(2L).cantidad(3)
                .precioUnitario(BigDecimal.valueOf(5500))
                .subtotal(BigDecimal.valueOf(16500))
                .build();

        when(detallePedidoService.getAllDetallePedido()).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/pedido/detallepedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.detallePedidoResponseList").isArray())
                .andExpect(jsonPath("$._embedded.detallePedidoResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.detallePedidoResponseList[0].cantidad").value(2))
                .andExpect(jsonPath("$._embedded.detallePedidoResponseList[1].id").value(2))
                .andExpect(jsonPath("$._embedded.detallePedidoResponseList[1].cantidad").value(3))
                .andExpect(jsonPath("$._links.self.href").isString());
        }

        @Test
        void getAll_shouldReturnEmptyList() throws Exception {
        when(detallePedidoService.getAllDetallePedido()).thenReturn(List.of());

        mockMvc.perform(get("/pedido/detallepedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links.self.href").isString());
        }

        @Test
        void getById_shouldReturnDetallePedidoResponse() throws Exception {
        DetallePedidoResponse d = DetallePedidoResponse.builder()
                .id(1L).productoId(10L).pedidoId(1L).cantidad(2)
                .precioUnitario(BigDecimal.valueOf(5200))
                .subtotal(BigDecimal.valueOf(10400))
                .build();

        when(detallePedidoService.getDetallePedidoById(1L)).thenReturn(d);

        mockMvc.perform(get("/pedido/detallepedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productoId").value(10))
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$._links.self.href").isString())
                .andExpect(jsonPath("$._links['todos los detallesPedido'].href").isString());
        }

        @Test
        void getById_shouldReturnNotFound() throws Exception {
        when(detallePedidoService.getDetallePedidoById(99L))
                .thenThrow(new ResourceNotFoundException("Detalle pedido no encontrado"));

        mockMvc.perform(get("/pedido/detallepedidos/99"))
                .andExpect(status().isNotFound());
        }

        @Test
        void createDetallePedido_shouldReturnDetallePedidoResponse() throws Exception {
        DetallePedidoResponse created = DetallePedidoResponse.builder()
                .id(1L).productoId(10L).pedidoId(1L).cantidad(2)
                .precioUnitario(BigDecimal.valueOf(5200))
                .subtotal(BigDecimal.valueOf(10400))
                .build();

        when(detallePedidoService.createDetalleResponse(any())).thenReturn(created);

        String body = """
                {
                        "pedidoId": 1,
                        "productoId": 10,
                        "cantidad": 2
                }
                """;

        mockMvc.perform(post("/pedido/detallepedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productoId").value(10))
                .andExpect(jsonPath("$.cantidad").value(2));
        }

        @Test
        void updateDetallePedido_shouldReturnUpdated() throws Exception {
        DetallePedidoResponse updated = DetallePedidoResponse.builder()
                .id(1L).productoId(10L).pedidoId(1L).cantidad(3)
                .precioUnitario(BigDecimal.valueOf(5200))
                .subtotal(BigDecimal.valueOf(15600))
                .build();

        when(detallePedidoService.updateDetallePedidoResponse(eq(1L), any())).thenReturn(updated);

        String body = """
                {
                        "pedidoId": 1,
                        "productoId": 10,
                        "cantidad": 3
                }
                """;

        mockMvc.perform(put("/pedido/detallepedidos/modificar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cantidad").value(3));
        }

        @Test
        void deleteDetallePedido_shouldReturnOk() throws Exception {
        doNothing().when(detallePedidoService).deleteDetallePedido(1L);

        mockMvc.perform(delete("/pedido/detallepedidos/eliminar/1"))
                .andExpect(status().isOk());

        verify(detallePedidoService).deleteDetallePedido(1L);
        }
}
