package com.nativa.pago_service.controller;

import com.nativa.pago_service.assemblers.PagoModelAssembler;
import com.nativa.pago_service.dto.PagoResponse;
import com.nativa.pago_service.exception.ResourceNotFoundException;
import com.nativa.pago_service.security.JwtUtil;
import com.nativa.pago_service.service.PagoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagoController.class)
@Import(PagoModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class PagoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private PagoService pagoService;

        @MockitoBean
        private JwtUtil jwtUtil;

        @Test
        void getAllPagos_shouldReturnList() throws Exception {
        PagoResponse pago1 = PagoResponse.builder()
                .id(1L).pedido_id(1L).usuario_id(1L).total(100.0)
                .metodoPago("TARJETA").fechaPago(LocalDateTime.now())
                .build();
        PagoResponse pago2 = PagoResponse.builder()
                .id(2L).pedido_id(2L).usuario_id(2L).total(200.0)
                .metodoPago("EFECTIVO").fechaPago(LocalDateTime.now())
                .build();

        when(pagoService.getAllPagos()).thenReturn(List.of(pago1, pago2));

        mockMvc.perform(get("/pago/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.pagoResponseList").isArray())
                .andExpect(jsonPath("$._embedded.pagoResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.pagoResponseList[0].metodoPago").value("TARJETA"))
                .andExpect(jsonPath("$._embedded.pagoResponseList[1].id").value(2))
                .andExpect(jsonPath("$._embedded.pagoResponseList[1].metodoPago").value("EFECTIVO"))
                .andExpect(jsonPath("$._links.self.href").isString());
}

        @Test
        void getAllPagos_shouldReturnEmptyList() throws Exception {
        when(pagoService.getAllPagos()).thenReturn(List.of());

        mockMvc.perform(get("/pago/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links.self.href").isString());
}

        @Test
        void getById_shouldReturnPagoResponse() throws Exception {
        PagoResponse pago = PagoResponse.builder()
                .id(1L).pedido_id(1L).usuario_id(1L).total(100.0)
                .metodoPago("TARJETA").fechaPago(LocalDateTime.now())
                .build();

        when(pagoService.findById(1L)).thenReturn(pago);

        mockMvc.perform(get("/pago/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"))
                .andExpect(jsonPath("$.total").value(100.0))
                .andExpect(jsonPath("$._links.self.href").isString())
                .andExpect(jsonPath("$._links.pagos.href").isString());
}

        @Test
        void getById_shouldReturnNotFound() throws Exception {
        when(pagoService.findById(99L)).thenThrow(new ResourceNotFoundException("Pago no encontrado"));

        mockMvc.perform(get("/pago/pagos/99"))
                .andExpect(status().isNotFound());
}

        @Test
        void createPago_shouldReturnCreated() throws Exception {
        PagoResponse created = PagoResponse.builder()
                .id(1L).pedido_id(1L).usuario_id(1L).total(100.0)
                .metodoPago("TARJETA").fechaPago(LocalDateTime.now())
                .build();

        when(pagoService.createPago(any())).thenReturn(created);

        String requestBody = """
                {
                        "pedido_id": 1,
                        "usuario_id": 1,
                        "metodoPago": "TARJETA"
                }
                """;

        mockMvc.perform(post("/pago/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"))
                .andExpect(jsonPath("$.total").value(100.0));
}
}
