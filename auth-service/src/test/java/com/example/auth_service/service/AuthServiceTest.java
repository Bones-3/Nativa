package com.example.auth_service.service;

import com.example.auth_service.model.Credencial;
import com.example.auth_service.repository.CredencialRepository;
import com.example.auth_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CredencialRepository credencialRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Credencial credencial;

    @BeforeEach
    void setUp() {
        credencial = new Credencial(1L, "usuario@nativa.test", "hashedPassword", true);
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        // Given
        when(credencialRepository.findByCorreoUser("usuario@nativa.test")).thenReturn(Optional.of(credencial));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generarToken("usuario@nativa.test")).thenReturn("token-generado");

        // When
        String result = authService.login("usuario@nativa.test", "password123");

        // Then
        assertThat(result).isEqualTo("token-generado");
        verify(jwtUtil).generarToken("usuario@nativa.test");
    }

    @Test
    void login_shouldReturnNull_whenUserDoesNotExist() {
        // Given
        when(credencialRepository.findByCorreoUser("noexiste@nativa.test")).thenReturn(Optional.empty());

        // When
        String result = authService.login("noexiste@nativa.test", "password123");

        // Then
        assertThat(result).isNull();
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_shouldReturnNull_whenUserIsInactive() {
        // Given
        credencial.setActivo(false);
        when(credencialRepository.findByCorreoUser("usuario@nativa.test")).thenReturn(Optional.of(credencial));

        // When
        String result = authService.login("usuario@nativa.test", "password123");

        // Then
        assertThat(result).isNull();
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_shouldReturnNull_whenPasswordIsIncorrect() {
        // Given
        when(credencialRepository.findByCorreoUser("usuario@nativa.test")).thenReturn(Optional.of(credencial));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // When
        String result = authService.login("usuario@nativa.test", "wrongPassword");

        // Then
        assertThat(result).isNull();
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void register_shouldReturnSuccessMessage_whenEmailIsNew() {
        // Given
        when(credencialRepository.findByCorreoUser("nuevo@nativa.test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        // When
        String result = authService.register("nuevo@nativa.test", "password123");

        // Then
        assertThat(result).isEqualTo("Usuario creado exitosamente");
        verify(credencialRepository).save(any(Credencial.class));
    }

    @Test
    void register_shouldReturnError_whenEmailAlreadyExists() {
        // Given
        when(credencialRepository.findByCorreoUser("usuario@nativa.test")).thenReturn(Optional.of(credencial));

        // When
        String result = authService.register("usuario@nativa.test", "password123");

        // Then
        assertThat(result).isEqualTo("El usuario ya existe");
        verify(credencialRepository, never()).save(any());
    }

    @Test
    void register_shouldReturnError_whenEmailIsInvalid() {
        // Given
        when(credencialRepository.findByCorreoUser("correo-invalido")).thenReturn(Optional.empty());

        // When
        String result = authService.register("correo-invalido", "password123");

        // Then
        assertThat(result).isEqualTo("Correo inválido");
        verify(credencialRepository, never()).save(any());
    }
}
