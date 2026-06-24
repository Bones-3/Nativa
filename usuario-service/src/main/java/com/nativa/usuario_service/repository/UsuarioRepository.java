package com.nativa.usuario_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nativa.usuario_service.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
        List<Usuario> findByEstadoUsuarioTrue();
}
