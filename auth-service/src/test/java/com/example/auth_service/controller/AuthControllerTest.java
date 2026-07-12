package com.example.auth_service.controller;

import com.example.auth_service.security.JwtUtil;
import com.example.auth_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthService authService;

        @MockitoBean
        private JwtUtil jwtUtil;

        @Test
        void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
        // Given
        when(authService.login("usuario@nativa.test", "password123")).thenReturn("token-generado");

        // When / Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "correo": "usuario@nativa.test",
                                "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Login exitoso"))
                .andExpect(jsonPath("$.token").value("token-generado"))
                .andExpect(jsonPath("$.correo").value("usuario@nativa.test"));
}

        @Test
        void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        // Given
        when(authService.login("usuario@nativa.test", "wrongPassword")).thenReturn(null);

        // When / Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "correo": "usuario@nativa.test",
                                "password": "wrongPassword"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
}

        @Test
        void register_shouldReturnSuccessMessage_whenEmailIsNew() throws Exception {
        // Given
        when(authService.register("nuevo@nativa.test", "password123")).thenReturn("Usuario creado exitosamente");

        // When / Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "correo": "nuevo@nativa.test",
                                "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario creado exitosamente"));
}

        @Test
        void register_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        // Given
        when(authService.register("usuario@nativa.test", "password123")).thenReturn("El usuario ya existe");

        // When / Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "correo": "usuario@nativa.test",
                                "password": "password123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El usuario ya existe"));
}
}
