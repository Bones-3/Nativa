package com.nativa.promocion_service;

import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nativa.promocion_service.model.Promocion;
import com.nativa.promocion_service.repository.PromocionRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PromocionRepository promocionRepository;
 
    @Override
    public void run(String... args) throws Exception {
        if (promocionRepository.count() > 10) {
            System.out.println("DataLoader ya fue ejecutado anteriormente. Omitiendo...");
            return;
        }
 
        Faker faker = new Faker();
        Random random = new Random();
 
        for (int i = 0; i < 5; i++) {
            Promocion promocion = new Promocion();
 
            promocion.setCodigo(faker.regexify("[A-Z]{5}[0-9]{3}"));
            promocion.setDescripcion(faker.lorem().sentence(random.nextInt(10) + 5));
            promocion.setPorcentajeDescuento(BigDecimal.valueOf(random.nextInt(50) + 5));
 
            LocalDate inicio = LocalDate.now().plusDays(random.nextInt(10));
            LocalDate fin = inicio.plusDays(random.nextInt(20) + 5);
            promocion.setFechaInicio(inicio);
            promocion.setFechaFin(fin);
 
            promocion.setActivo(random.nextBoolean());
 
            promocionRepository.save(promocion);
        }
 
        System.out.println("Se han cargado 5 promociones de prueba correctamente en promocion-service!");
    }
}
