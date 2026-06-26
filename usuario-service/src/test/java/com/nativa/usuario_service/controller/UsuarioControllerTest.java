package com.nativa.usuario_service.controller;

import com.nativa.usuario_service.assemblers.UsuarioModelAssembler;
import com.nativa.usuario_service.dto.UsuarioResponse;
import com.nativa.usuario_service.exception.ResourceNotFoundException;
import com.nativa.usuario_service.security.JwtUtil;
import com.nativa.usuario_service.service.UsuarioService;

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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import(UsuarioModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void getUsuarioActivo_shouldReturnList() throws Exception {
        UsuarioResponse u1 = UsuarioResponse.builder()
                .id(1L).correoUsuario("test@test.com").nombre("Juan")
                .apellido("Perez").telefonoUsuario("123456789").estadoUsuario(true)
                .build();
        UsuarioResponse u2 = UsuarioResponse.builder()
                .id(2L).correoUsuario("otro@test.com").nombre("Maria")
                .apellido("Gomez").telefonoUsuario("987654321").estadoUsuario(true)
                .build();

        when(usuarioService.getUsuariosActivo()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/usuario/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usuarioResponseList").isArray())
                .andExpect(jsonPath("$._embedded.usuarioResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.usuarioResponseList[0].correoUsuario").value("test@test.com"))
                .andExpect(jsonPath("$._embedded.usuarioResponseList[1].id").value(2))
                .andExpect(jsonPath("$._embedded.usuarioResponseList[1].correoUsuario").value("otro@test.com"))
                .andExpect(jsonPath("$._links.self.href").isString());
    }

    @Test
    void getUsuarioActivo_shouldReturnEmptyList() throws Exception {
        when(usuarioService.getUsuariosActivo()).thenReturn(List.of());

        mockMvc.perform(get("/usuario/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links.self.href").isString());
    }

    @Test
    void getAllUsuario_shouldReturnList() throws Exception {
        UsuarioResponse u1 = UsuarioResponse.builder()
                .id(1L).correoUsuario("test@test.com").nombre("Juan")
                .apellido("Perez").telefonoUsuario("123456789").estadoUsuario(true)
                .build();

        when(usuarioService.getAllUsuarios()).thenReturn(List.of(u1));

        mockMvc.perform(get("/usuario/usuarios/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usuarioResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.usuarioResponseList[0].nombre").value("Juan"))
                .andExpect(jsonPath("$._links.self.href").isString());
    }

    @Test
    void getAllUsuario_shouldReturnEmptyList() throws Exception {
        when(usuarioService.getAllUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/usuario/usuarios/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links.self.href").isString());
    }

    @Test
    void getUsuarioById_shouldReturnUser() throws Exception {
        UsuarioResponse u = UsuarioResponse.builder()
                .id(1L).correoUsuario("test@test.com").nombre("Juan")
                .apellido("Perez").telefonoUsuario("123456789").estadoUsuario(true)
                .build();

        when(usuarioService.getUsuarioById(1L)).thenReturn(u);

        mockMvc.perform(get("/usuario/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.correoUsuario").value("test@test.com"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"))
                .andExpect(jsonPath("$.estadoUsuario").value(true))
                .andExpect(jsonPath("$._links.self.href").isString())
                .andExpect(jsonPath("$._links['usuarios activos'].href").isString())
                .andExpect(jsonPath("$._links['todos los usuarios'].href").isString());
    }

    @Test
    void getUsuarioById_shouldReturn404() throws Exception {
        when(usuarioService.getUsuarioById(99L)).thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/usuario/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
    }

    @Test
    void createUsuario_shouldReturn200() throws Exception {
        UsuarioResponse created = UsuarioResponse.builder()
                .id(1L).correoUsuario("test@test.com").nombre("Juan")
                .apellido("Perez").telefonoUsuario("123456789").estadoUsuario(true)
                .build();

        when(usuarioService.createUsuario(any())).thenReturn(created);

        String body = """
                {
                "correoUsuario": "test@test.com",
                "nombres": "Juan",
                "apellidos": "Perez",
                "telefonoUsuario": "123456789"
                }
                """;

        mockMvc.perform(post("/usuario/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.correoUsuario").value("test@test.com"));
    }

    @Test
    void updateUsuario_shouldReturn200() throws Exception {
        UsuarioResponse updated = UsuarioResponse.builder()
                .id(1L).correoUsuario("updated@test.com").nombre("Maria")
                .apellido("Gomez").telefonoUsuario("987654321").estadoUsuario(true)
                .build();

        when(usuarioService.updateUsuario(any(), any())).thenReturn(updated);

        String body = """
                {
                "correoUsuario": "updated@test.com",
                "nombres": "Maria",
                "apellidos": "Gomez",
                "telefonoUsuario": "987654321"
                }
                """;

        mockMvc.perform(put("/usuario/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.correoUsuario").value("updated@test.com"))
                .andExpect(jsonPath("$.nombre").value("Maria"));
    }

    @Test
    void deleteUsuario_shouldReturn204() throws Exception {
        doNothing().when(usuarioService).deleteUsuario(1L);

        mockMvc.perform(delete("/usuario/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService).deleteUsuario(1L);
    }
}
