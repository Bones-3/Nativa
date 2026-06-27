package com.example.inventario;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner{
    
    private final InventarioRepository inventarioRepository;

    @Override
    public void run(String... args) throws Exception {

        // ✅ Liquibase ya sembró 7 productos (changeset:2).
        // Si hay más de 7, significa que este DataLoader ya corrió antes → salir.
        if (inventarioRepository.count() > 7) {
            System.out.println("DataLoader ya fue ejecutado anteriormente. Omitiendo...");
            return;
        }

        Faker faker = new Faker();
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            Inventario inventario = new Inventario();

            inventario.setProductoId((long) (i + 1));
            inventario.setNombreProducto(faker.commerce().productName());
            inventario.setStockActual(random.nextInt(50) + 10); // Entre 10 y 60
            inventario.setStockMinimo(5); 
            inventario.setUnidadMedida("UNIDADES");
            inventario.setUltimaActualizacion(LocalDateTime.now());

            inventarioRepository.save(inventario);
        }

        System.out.println("¡Se han cargado 5 productos de prueba correctamente en inventario_service!");
    }
}
