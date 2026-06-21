package com.nativa.usuario_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nativa.usuario_service.dto.UsuarioRequest;
import com.nativa.usuario_service.dto.UsuarioResponse;
import com.nativa.usuario_service.mapper.UsuarioMapper;
import com.nativa.usuario_service.model.Usuario;
import com.nativa.usuario_service.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAllUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public UsuarioResponse createUsuario(UsuarioRequest request) {
        var usuario = usuarioMapper.toEntity(request);
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse updateUsuario(Long id,UsuarioRequest request) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        usuario.setCorreoUsuario(request.getCorreoUsuario());
        usuario.setNombre(request.getNombre());  
        usuario.setApellido(request.getApellido());
        usuario.setTelefonoUsuario(request.getTelefonoUsuario());

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deleteUsuario(Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        usuario.setEstadoUsuario(false);
        usuarioRepository.save(usuario);
    }

}
