package com.nativa.usuario_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nativa.usuario_service.assemblers.UsuarioModelAssembler;
import com.nativa.usuario_service.dto.UsuarioRequest;
import com.nativa.usuario_service.dto.UsuarioResponse;
import com.nativa.usuario_service.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/usuario/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioModelAssembler assembler;

    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<UsuarioResponse>>> getUsuarioActivo() {
        log.info("Petición HTTP GET recibida en /usuario/usuarios - Listando usuarios activos");
        
        List<EntityModel<UsuarioResponse>> usuario = usuarioService.getUsuariosActivo()
                .stream()
                .map(assembler::toModel)
                .toList();
        
        log.info("Se retornaron {} usuarios activos exitosamente", usuario.size());
        return ResponseEntity.ok(CollectionModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).getUsuarioActivo()).withSelfRel()));
    }

    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<UsuarioResponse>>> getAllUsuario() {
        log.info("Petición HTTP GET recibida en /usuario/usuarios/all - Listando todos los usuarios");
        
        List<EntityModel<UsuarioResponse>> usuario = usuarioService.getAllUsuarios()
                .stream()
                .map(assembler::toModel)
                .toList();
        
        log.info("Se retornaron {} usuarios totales exitosamente", usuario.size());
        return ResponseEntity.ok(CollectionModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).getAllUsuario()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponse>> getUsuarioById(@PathVariable Long id) {    
        log.info("Petición HTTP GET recibida en /usuario/usuarios/{} - Buscando usuario", id);
        
        var usuarioResponse = usuarioService.getUsuarioById(id);
        
        log.info("Usuario con ID {} encontrado correctamente", id);
        return ResponseEntity.ok(assembler.toModel(usuarioResponse));
    }

    @PostMapping()
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        log.info("Petición HTTP POST recibida en /usuario/usuarios - Datos recibidos para correo: {}", usuarioRequest.getCorreoUsuario());
        
        UsuarioResponse creado = usuarioService.createUsuario(usuarioRequest);
        
        log.info("Respuesta enviada de usuario creado exitosamente");
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequest usuarioRequest) {
        log.info("Petición HTTP PUT recibida en /usuario/usuarios/{} - Actualizando datos", id);
        
        UsuarioResponse actualizado = usuarioService.updateUsuario(id, usuarioRequest);
        
        log.info("Respuesta enviada de usuario con ID {} actualizado exitosamente", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        log.info("Petición HTTP DELETE recibida en /usuario/usuarios/{} - Eliminación lógica", id);
        
        usuarioService.deleteUsuario(id);
        
        log.info("Usuario con ID {} eliminado. Retornando status 24 (No Content)", id);
        return ResponseEntity.noContent().build();
    }
    
}
