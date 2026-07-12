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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/usuario/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuario", description = "Operaciones relacionadas con los usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioModelAssembler assembler;

    @Operation(summary = "Obtener usuarios activos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios activos obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    })
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

    @Operation(summary = "Obtener todos los usuarios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de todos los usuarios obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    })
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

    @Operation(summary = "Obtener usuario por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponse>> getUsuarioById(@PathVariable Long id) {
        log.info("Petición HTTP GET recibida en /usuario/usuarios/{} - Buscando usuario", id);

        var usuarioResponse = usuarioService.getUsuarioById(id);

        log.info("Usuario con ID {} encontrado correctamente", id);
        return ResponseEntity.ok(assembler.toModel(usuarioResponse));
    }

    @Operation(summary = "Crear usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    })
    @PostMapping()
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        log.info("Petición HTTP POST recibida en /usuario/usuarios - Datos recibidos para correo: {}", usuarioRequest.getCorreoUsuario());

        UsuarioResponse creado = usuarioService.createUsuario(usuarioRequest);

        log.info("Respuesta enviada de usuario creado exitosamente");
        return ResponseEntity.ok(creado);
    }

    @Operation(summary = "Actualizar usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequest usuarioRequest) {
        log.info("Petición HTTP PUT recibida en /usuario/usuarios/{} - Actualizando datos", id);

        UsuarioResponse actualizado = usuarioService.updateUsuario(id, usuarioRequest);

        log.info("Respuesta enviada de usuario con ID {} actualizado exitosamente", id);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        log.info("Petición HTTP DELETE recibida en /usuario/usuarios/{} - Eliminación lógica", id);

        usuarioService.deleteUsuario(id);

        log.info("Usuario con ID {} eliminado. Retornando status 24 (No Content)", id);
        return ResponseEntity.noContent().build();
    }

}
