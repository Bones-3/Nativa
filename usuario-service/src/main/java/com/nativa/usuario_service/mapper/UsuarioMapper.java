package com.nativa.usuario_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.usuario_service.dto.UsuarioRequest;
import com.nativa.usuario_service.dto.UsuarioResponse;
import com.nativa.usuario_service.model.Usuario;

@Component
public class UsuarioMapper {
    public Usuario toEntity(UsuarioRequest request) {

        Usuario usuario = new Usuario();

        usuario.setCorreoUsuario(request.getCorreoUsuario());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefonoUsuario(request.getTelefonoUsuario());
        usuario.setEstadoUsuario(true);

        return usuario;   
    }
    
    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .correoUsuario(usuario.getCorreoUsuario())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .telefonoUsuario(usuario.getTelefonoUsuario())
                .estadoUsuario(usuario.getEstadoUsuario())
                .build();
    }
}
