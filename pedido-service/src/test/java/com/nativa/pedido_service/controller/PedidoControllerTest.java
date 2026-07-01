package com.nativa.pedido_service.controller;

import com.nativa.pedido_service.assemblers.PedidoModelAssembler;
import com.nativa.pedido_service.dto.PedidoResponse;
import com.nativa.pedido_service.exception.ResourceNotFoundException;
import com.nativa.pedido_service.security.JwtUtil;
import com.nativa.pedido_service.service.PedidoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@Import(PedidoModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private PedidoService pedidoService;

        @MockitoBean
        private JwtUtil jwtUtil;

        @Test
        void getAllPedidos_shouldReturnList() throws Exception {
                PedidoResponse p1 = PedidoResponse.builder()
                        .id(1L).usuarioId(1L).tipoEntrega("DELIVERY")
                        .fechaPedido(LocalDateTime.now()).totalPagar(BigDecimal.valueOf(50000))
                        .build();
                PedidoResponse p2 = PedidoResponse.builder()
                        .id(2L).usuarioId(2L).tipoEntrega("RETIRO")
                        .fechaPedido(LocalDateTime.now()).totalPagar(BigDecimal.valueOf(30000))
                        .build();

                when(pedidoService.getAllPedido()).thenReturn(List.of(p1, p2));

                mockMvc.perform(get("/pedido/pedidos"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._embedded.pedidoResponseList").isArray())
                        .andExpect(jsonPath("$._embedded.pedidoResponseList[0].id").value(1))
                        .andExpect(jsonPath("$._embedded.pedidoResponseList[0].tipoEntrega").value("DELIVERY"))
                        .andExpect(jsonPath("$._embedded.pedidoResponseList[1].id").value(2))
                        .andExpect(jsonPath("$._embedded.pedidoResponseList[1].tipoEntrega").value("RETIRO"))
                        .andExpect(jsonPath("$._links.self.href").isString());
        }

        @Test
        void getAllPedidos_shouldReturnEmptyList() throws Exception {
                when(pedidoService.getAllPedido()).thenReturn(List.of());

                mockMvc.perform(get("/pedido/pedidos"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._embedded").doesNotExist())
                        .andExpect(jsonPath("$._links.self.href").isString());
        }

        @Test
        void getPedidoById_shouldReturnPedidoResponse() throws Exception {
                PedidoResponse p = PedidoResponse.builder()
                        .id(1L).usuarioId(1L).tipoEntrega("DELIVERY")
                        .fechaPedido(LocalDateTime.now()).totalPagar(BigDecimal.valueOf(50000))
                        .build();

                when(pedidoService.getPedidoById(1L)).thenReturn(p);

                mockMvc.perform(get("/pedido/pedidos/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.tipoEntrega").value("DELIVERY"))
                        .andExpect(jsonPath("$._links.self.href").isString())
                        .andExpect(jsonPath("$._links.detallesPedido.href").isString());
        }

        @Test
        void getPedidoById_shouldReturnNotFound() throws Exception {
                when(pedidoService.getPedidoById(99L)).thenThrow(new ResourceNotFoundException("Pedido no encontrado"));

                mockMvc.perform(get("/pedido/pedidos/99"))
                        .andExpect(status().isNotFound());
        }

        @Test
        void createPedido_shouldReturnPedidoResponse() throws Exception {
                PedidoResponse created = PedidoResponse.builder()
                        .id(1L).usuarioId(1L).tipoEntrega("DELIVERY")
                        .fechaPedido(LocalDateTime.now()).totalPagar(BigDecimal.ZERO)
                        .build();

                when(pedidoService.createPedidoResponse(any())).thenReturn(created);

                String body = """
                        {
                        "usuarioId": 1,
                        "tipoEntrega": "DELIVERY"
                        }
                        """;

                mockMvc.perform(post("/pedido/pedidos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.tipoEntrega").value("DELIVERY"));
        }

        @Test
        void deletePedido_shouldReturnOk() throws Exception {
                doNothing().when(pedidoService).deletePedido(1L);

                mockMvc.perform(delete("/pedido/pedidos/eliminar/1"))
                        .andExpect(status().isNoContent());

                verify(pedidoService).deletePedido(1L);
        }
        }
