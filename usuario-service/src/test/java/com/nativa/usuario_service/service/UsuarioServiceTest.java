package com.nativa.usuario_service.service;

import com.nativa.usuario_service.dto.UsuarioRequest;
import com.nativa.usuario_service.dto.UsuarioResponse;
import com.nativa.usuario_service.exception.ResourceNotFoundException;
import com.nativa.usuario_service.mapper.UsuarioMapper;
import com.nativa.usuario_service.model.Usuario;
import com.nativa.usuario_service.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioResponse usuarioResponse;
    private UsuarioRequest usuarioRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreoUsuario("test@test.com");
        usuario.setNombres("Juan");
        usuario.setApellidos("Perez");
        usuario.setTelefonoUsuario("123456789");
        usuario.setEstadoUsuario(true);

        usuarioResponse = UsuarioResponse.builder()
                .id(1L)
                .correoUsuario("test@test.com")
                .nombre("Juan")
                .apellido("Perez")
                .telefonoUsuario("123456789")
                .estadoUsuario(true)
                .build();

        usuarioRequest = new UsuarioRequest();
        usuarioRequest.setCorreoUsuario("test@test.com");
        usuarioRequest.setNombres("Juan");
        usuarioRequest.setApellidos("Perez");
        usuarioRequest.setTelefonoUsuario("123456789");
    }

    @Test
    void getUsuariosActivo_shouldReturnList() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(2L);
        otroUsuario.setEstadoUsuario(true);
        when(usuarioRepository.findByEstadoUsuarioTrue()).thenReturn(List.of(usuario, otroUsuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(usuarioResponse);

        UsuarioResponse otroResponse = UsuarioResponse.builder().id(2L).estadoUsuario(true).build();
        when(usuarioMapper.toResponse(otroUsuario)).thenReturn(otroResponse);

        List<UsuarioResponse> result = usuarioService.getUsuariosActivo();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(usuarioRepository).findByEstadoUsuarioTrue();
        verify(usuarioMapper).toResponse(usuario);
        verify(usuarioMapper).toResponse(otroUsuario);
    }

    @Test
    void getUsuariosActivo_shouldReturnEmptyList() {
        when(usuarioRepository.findByEstadoUsuarioTrue()).thenReturn(List.of());

        List<UsuarioResponse> result = usuarioService.getUsuariosActivo();

        assertThat(result).isEmpty();
        verify(usuarioRepository).findByEstadoUsuarioTrue();
        verifyNoInteractions(usuarioMapper);
    }

    @Test
    void getAllUsuarios_shouldReturnList() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(2L);
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario, otroUsuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(usuarioResponse);

        UsuarioResponse otroResponse = UsuarioResponse.builder().id(2L).build();
        when(usuarioMapper.toResponse(otroUsuario)).thenReturn(otroResponse);

        List<UsuarioResponse> result = usuarioService.getAllUsuarios();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(usuarioRepository).findAll();
        verify(usuarioMapper).toResponse(usuario);
        verify(usuarioMapper).toResponse(otroUsuario);
    }

    @Test
    void getAllUsuarios_shouldReturnEmptyList() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        List<UsuarioResponse> result = usuarioService.getAllUsuarios();

        assertThat(result).isEmpty();
        verify(usuarioRepository).findAll();
        verifyNoInteractions(usuarioMapper);
    }

    @Test
    void getUsuarioById_shouldReturnUser() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(usuarioResponse);

        UsuarioResponse result = usuarioService.getUsuarioById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCorreoUsuario()).isEqualTo("test@test.com");
        assertThat(result.getNombre()).isEqualTo("Juan");
        assertThat(result.getApellido()).isEqualTo("Perez");
        verify(usuarioRepository).findById(1L);
        verify(usuarioMapper).toResponse(usuario);
    }

    @Test
    void getUsuarioById_shouldThrowNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.getUsuarioById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");

        verify(usuarioRepository).findById(99L);
        verifyNoInteractions(usuarioMapper);
    }

    @Test
    void createUsuario_shouldReturnSavedUser() {
        Usuario entityToSave = new Usuario();
        entityToSave.setCorreoUsuario("test@test.com");
        entityToSave.setNombres("Juan");
        entityToSave.setApellidos("Perez");
        entityToSave.setTelefonoUsuario("123456789");
        entityToSave.setEstadoUsuario(true);

        when(usuarioMapper.toEntity(usuarioRequest)).thenReturn(entityToSave);

        Usuario savedEntity = new Usuario();
        savedEntity.setId(1L);
        savedEntity.setCorreoUsuario("test@test.com");
        savedEntity.setNombres("Juan");
        savedEntity.setApellidos("Perez");
        savedEntity.setTelefonoUsuario("123456789");
        savedEntity.setEstadoUsuario(true);

        when(usuarioRepository.save(entityToSave)).thenReturn(savedEntity);
        when(usuarioMapper.toResponse(savedEntity)).thenReturn(usuarioResponse);

        UsuarioResponse result = usuarioService.createUsuario(usuarioRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCorreoUsuario()).isEqualTo("test@test.com");
        verify(usuarioMapper).toEntity(usuarioRequest);
        verify(usuarioRepository).save(entityToSave);
        verify(usuarioMapper).toResponse(savedEntity);
    }

    @Test
    void updateUsuario_shouldReturnUpdatedUser() {
        UsuarioRequest updateRequest = new UsuarioRequest();
        updateRequest.setCorreoUsuario("updated@test.com");
        updateRequest.setNombres("Maria");
        updateRequest.setApellidos("Gomez");
        updateRequest.setTelefonoUsuario("987654321");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario savedEntity = new Usuario();
        savedEntity.setId(1L);
        savedEntity.setCorreoUsuario("updated@test.com");
        savedEntity.setNombres("Maria");
        savedEntity.setApellidos("Gomez");
        savedEntity.setTelefonoUsuario("987654321");
        savedEntity.setEstadoUsuario(true);

        when(usuarioRepository.save(usuario)).thenReturn(savedEntity);

        UsuarioResponse updatedResponse = UsuarioResponse.builder()
                .id(1L).correoUsuario("updated@test.com").nombre("Maria")
                .apellido("Gomez").telefonoUsuario("987654321").estadoUsuario(true)
                .build();
        when(usuarioMapper.toResponse(savedEntity)).thenReturn(updatedResponse);

        UsuarioResponse result = usuarioService.updateUsuario(1L, updateRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCorreoUsuario()).isEqualTo("updated@test.com");
        assertThat(result.getNombre()).isEqualTo("Maria");
        assertThat(result.getApellido()).isEqualTo("Gomez");
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(usuario);
        verify(usuarioMapper).toResponse(savedEntity);
    }

    @Test
    void updateUsuario_shouldThrowNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.updateUsuario(99L, usuarioRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoría no encontrada");

        verify(usuarioRepository).findById(99L);
        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(usuarioMapper);
    }

    @Test
    void deleteUsuario_shouldSetEstadoFalse() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.deleteUsuario(1L);

        assertThat(usuario.getEstadoUsuario()).isFalse();
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deleteUsuario_shouldThrowNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.deleteUsuario(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoría no encontrada");

        verify(usuarioRepository).findById(99L);
        verify(usuarioRepository, never()).save(any());
    }
}
