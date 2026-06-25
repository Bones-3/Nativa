package com.nativa.resena_service;

import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nativa.resena_service.model.Resena;
import com.nativa.resena_service.repository.ResenaRepository;

import lombok.RequiredArgsConstructor;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ResenaRepository resenaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (resenaRepository.count() > 0) {
            System.out.println("DataLoader ya fue ejecutado anteriormente. Omitiendo...");
            return;
        }

        Faker faker = new Faker();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            Resena resena = new Resena();
            resena.setProductoId((long) (random.nextInt(15) + 1));
            resena.setUsuarioId((long) (random.nextInt(15) + 1));
            resena.setComentario(faker.lorem().sentence(random.nextInt(20) + 5));
            resena.setCalificacion(random.nextInt(5) + 1);
            resenaRepository.save(resena);
        }

        System.out.println("Se han cargado 20 resenas de prueba correctamente en resena-service!");
    }
}
