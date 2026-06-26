package com.nativa.usuario_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nativa.usuario_service.dto.UsuarioRequest;
import com.nativa.usuario_service.dto.UsuarioResponse;
import com.nativa.usuario_service.exception.ResourceNotFoundException;
import com.nativa.usuario_service.mapper.UsuarioMapper;
import com.nativa.usuario_service.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> getUsuariosActivo() {
        log.info("Solicitando lista de usuarios activos");
        return usuarioRepository.findByEstadoUsuarioTrue()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAllUsuarios() {
        log.info("Solicitando la lista de todos los usuarios registrados");
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse getUsuarioById(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontró el usuario con ID: {}", id);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });
    }

    @Transactional
    public UsuarioResponse createUsuario(UsuarioRequest request) {
        log.info("Iniciando la creación de un nuevo usuario con correo: {}", request.getCorreoUsuario());
        var usuario = usuarioMapper.toEntity(request);
        var usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getId());
        return usuarioMapper.toResponse(usuarioGuardado);
    }

    @Transactional
    public UsuarioResponse updateUsuario(Long id, UsuarioRequest request) {
        log.info("Iniciando actualización del usuario con ID: {}", id);
        
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar: Usuario con ID {} no existe", id);
                    return new ResourceNotFoundException("Usuario no encontrado"); // Corregido el mensaje "Categoría no encontrada"
                });
        
        usuario.setCorreoUsuario(request.getCorreoUsuario());
        usuario.setNombres(request.getNombres());  
        usuario.setApellidos(request.getApellidos());
        usuario.setTelefonoUsuario(request.getTelefonoUsuario());

        var usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Usuario con ID: {} actualizado correctamente", id);
        return usuarioMapper.toResponse(usuarioActualizado);
    }

    @Transactional
    public void deleteUsuario(Long id) {
        log.info("Iniciando eliminación lógica del usuario con ID: {}", id);
        
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al eliminar: Usuario con ID {} no existe", id);
                    return new ResourceNotFoundException("Usuario no encontrado"); // Corregido el mensaje "Categoría no encontrada"
                });

        usuario.setEstadoUsuario(false);
        usuarioRepository.save(usuario);
        log.info("Usuario con ID: {} deshabilitado (Eliminación lógica completada)", id);
    }

}
