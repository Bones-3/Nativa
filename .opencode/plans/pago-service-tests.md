# Pago-Service Test Implementation Plan

## Files to Create/Modify

### 1. `pom.xml` — Add H2 test dependency
Insert after line 134 (`spring-boot-starter-webmvc-test` closing `</dependency>`):
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### 2. `src/test/resources/application.yaml` — CREATE (new file)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.H2Dialect
  flyway:
    enabled: false

jwt:
  secret: a98ba831524d428bdb59b2a9342de0716eaa901463593fb888e58001f522c74d
  expiration: 86400000

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### 3. `src/test/java/.../service/PagoServiceTest.java` — CREATE (7 tests)

```java
package com.nativa.pago_service.service;

import com.nativa.pago_service.client.PedidoClient;
import com.nativa.pago_service.client.UsuarioClient;
import com.nativa.pago_service.client.dto.PedidoResponse;
import com.nativa.pago_service.client.dto.UsuarioResponse;
import com.nativa.pago_service.dto.PagoRequest;
import com.nativa.pago_service.dto.PagoResponse;
import com.nativa.pago_service.exception.ResourceNotFoundException;
import com.nativa.pago_service.mapper.PagoMapper;
import com.nativa.pago_service.model.Pago;
import com.nativa.pago_service.repository.PagoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PagoMapper pagoMapper;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private PagoService pagoService;

    private Pago pago;
    private PagoResponse pagoResponse;
    private PagoRequest pagoRequest;

    @BeforeEach
    void setUp() {
        pago = new Pago(1L, 1L, 1L, 100.0, "TARJETA", LocalDateTime.now());

        pagoResponse = PagoResponse.builder()
                .id(1L).pedido_id(1L).usuario_id(1L).total(100.0)
                .metodoPago("TARJETA").fechaPago(pago.getFechaPago())
                .build();

        pagoRequest = new PagoRequest();
        pagoRequest.setPedido_id(1L);
        pagoRequest.setUsuario_id(1L);
        pagoRequest.setMetodoPago("TARJETA");
    }

    @Test
    void getAllPagos_shouldReturnList() {
        Pago otroPago = new Pago(2L, 2L, 2L, 200.0, "EFECTIVO", LocalDateTime.now());
        when(pagoRepository.findAll()).thenReturn(List.of(pago, otroPago));
        when(pagoMapper.toResponse(pago)).thenReturn(pagoResponse);

        PagoResponse otroResponse = PagoResponse.builder()
                .id(2L).pedido_id(2L).usuario_id(2L).total(200.0)
                .metodoPago("EFECTIVO").fechaPago(otroPago.getFechaPago())
                .build();
        when(pagoMapper.toResponse(otroPago)).thenReturn(otroResponse);

        List<PagoResponse> result = pagoService.getAllPagos();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(pagoRepository).findAll();
        verify(pagoMapper).toResponse(pago);
        verify(pagoMapper).toResponse(otroPago);
    }

    @Test
    void getAllPagos_shouldReturnEmptyList() {
        when(pagoRepository.findAll()).thenReturn(List.of());

        List<PagoResponse> result = pagoService.getAllPagos();

        assertThat(result).isEmpty();
        verify(pagoRepository).findAll();
        verifyNoInteractions(pagoMapper);
    }

    @Test
    void findById_shouldReturnPagoResponse() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoMapper.toResponse(pago)).thenReturn(pagoResponse);

        PagoResponse result = pagoService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotal()).isEqualTo(100.0);
        verify(pagoRepository).findById(1L);
        verify(pagoMapper).toResponse(pago);
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        // Verifies current buggy behavior: .get() on empty Optional throws NoSuchElementException
        assertThatThrownBy(() -> pagoService.findById(99L))
                .isInstanceOf(NoSuchElementException.class);

        verify(pagoRepository).findById(99L);
        verifyNoInteractions(pagoMapper);
    }

    @Test
    void createPago_shouldReturnPagoResponse() {
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setId(1L);
        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuario);

        PedidoResponse pedido = new PedidoResponse();
        pedido.setId(1L);
        pedido.setTotalPagar(new BigDecimal("100.00"));
        when(pedidoClient.obtenerPorId(1L)).thenReturn(pedido);

        Pago pagoEntity = new Pago();
        when(pagoMapper.toEntity(pagoRequest)).thenReturn(pagoEntity);

        Pago savedPago = new Pago(1L, 1L, 1L, 100.0, "TARJETA", LocalDateTime.now());
        when(pagoRepository.save(pagoEntity)).thenReturn(savedPago);

        PagoResponse expectedResponse = PagoResponse.builder()
                .id(1L).pedido_id(1L).usuario_id(1L).total(100.0)
                .metodoPago("TARJETA").fechaPago(savedPago.getFechaPago())
                .build();
        when(pagoMapper.toResponse(savedPago)).thenReturn(expectedResponse);

        PagoResponse result = pagoService.createPago(pagoRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotal()).isEqualTo(100.0);
        verify(usuarioClient).obtenerPorId(1L);
        verify(pedidoClient).obtenerPorId(1L);
        verify(pagoMapper).toEntity(pagoRequest);
        verify(pagoRepository).save(pagoEntity);
        verify(pagoMapper).toResponse(savedPago);
    }

    @Test
    void createPago_shouldThrowException_whenUsuarioNotFound() {
        when(usuarioClient.obtenerPorId(1L)).thenReturn(null);

        assertThatThrownBy(() -> pagoService.createPago(pagoRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");

        verify(usuarioClient).obtenerPorId(1L);
        verifyNoInteractions(pedidoClient, pagoMapper, pagoRepository);
    }

    @Test
    void createPago_shouldThrowException_whenPedidoNotFound() {
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setId(1L);
        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuario);

        when(pedidoClient.obtenerPorId(1L)).thenReturn(null);

        assertThatThrownBy(() -> pagoService.createPago(pagoRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pedido no encontrado");

        verify(usuarioClient).obtenerPorId(1L);
        verify(pedidoClient).obtenerPorId(1L);
        verifyNoInteractions(pagoMapper, pagoRepository);
    }
}
```

### 4. `src/test/java/.../controller/PagoControllerTest.java` — CREATE (5 tests)

```java
package com.nativa.pago_service.controller;

import com.nativa.pago_service.assemblers.PagoModelAssembler;
import com.nativa.pago_service.dto.PagoResponse;
import com.nativa.pago_service.exception.GlobalExceptionHandler;
import com.nativa.pago_service.security.JwtUtil;
import com.nativa.pago_service.service.PagoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagoController.class)
@Import({PagoModelAssembler.class, GlobalExceptionHandler.class})
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
        when(pagoService.findById(99L)).thenThrow(NoSuchElementException.class);

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
```

## Test Summary

| File | Tests | Coverage |
|---|---|---|
| `PagoServiceTest` | 7 | `getAllPagos` (2), `findById` (2), `createPago` (3) |
| `PagoControllerTest` | 5 | `GET /pago/pagos` (2), `GET /pago/pagos/{id}` (2), `POST /pago/pagos` (1) |
| **Total** | **12** | |

## Verification

Run: `mvn test -pl pago-service`
