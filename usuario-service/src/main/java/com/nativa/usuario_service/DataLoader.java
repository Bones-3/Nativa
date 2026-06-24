package com.nativa.usuario_service;

import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nativa.usuario_service.model.Usuario;
import com.nativa.usuario_service.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    
    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {

        // ✅ Liquibase ya sembró 10 usuarios (changeset:2).
        // Si hay más de 10, significa que este DataLoader ya corrió antes → salir.
        if (usuarioRepository.count() > 10) {
            System.out.println("DataLoader ya fue ejecutado anteriormente. Omitiendo...");
            return;
        }

        Faker faker = new Faker();
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            Usuario usuario = new Usuario();

            String nombre = faker.name().firstName();
            String apellido = faker.name().lastName();
            String correo = faker.internet().emailAddress(
                nombre.toLowerCase() + "." + apellido.toLowerCase()
            );
            String telefono = faker.phoneNumber().cellPhone();
            Boolean estado = (random.nextInt(101) / 100.0) > 0.20;

            usuario.setNombres(nombre);
            usuario.setApellidos(apellido);
            usuario.setCorreoUsuario(correo);
            usuario.setTelefonoUsuario(telefono);
            usuario.setEstadoUsuario(estado);

            usuarioRepository.save(usuario);
        }

        System.out.println("¡Se han cargado 5 usuarios de prueba correctamente en usuario_service!");
    }
}