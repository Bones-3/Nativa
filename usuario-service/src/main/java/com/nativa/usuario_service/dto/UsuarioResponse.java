package com.nativa.usuario_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioResponse {
    private Long id;
    private String correoUsuario;
    private String nombre;   
    private String apellido;
    private String telefonoUsuario;
    private boolean estadoUsuario;
}
