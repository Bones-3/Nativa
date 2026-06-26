package com.nativa.pago_service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nativa.pago_service.model.Pago;
import com.nativa.pago_service.repository.PagoRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PagoRepository pagoRepository;

    @Override
    public void run(String... args) throws Exception {

        // ✅ flyway ya sembró 10 pagos (changeset:2).
        // Si hay más de 10, significa que este DataLoader ya corrió antes → salir.
        if (pagoRepository.count() > 10) {
            System.out.println("DataLoader ya fue ejecutado anteriormente. Omitiendo...");
            return;
        }

        Faker faker = new Faker();
        Random random = new Random();

        String[] metodosPago = {"TARJETA_DEBITO", "TARJETA_CREDITO", "EFECTIVO", "TRANSFERENCIA"};

        for (int i = 0; i < 5; i++) {
            Pago pago = new Pago();

            long pedidoIdAleatorio = random.nextLong(10) + 1;
            long usuarioIdAleatorio = random.nextLong(10) + 1;

            pago.setPedido_id(pedidoIdAleatorio);
            pago.setUsuario_id(usuarioIdAleatorio);
            
            double montoAleatorio = faker.number().randomDouble(2, 5000, 50000);
            pago.setTotal(montoAleatorio);
            
            // Selecciona un método de pago aleatorio del arreglo
            pago.setMetodoPago(metodosPago[random.nextInt(metodosPago.length)]);
            
            // Genera una fecha aleatoria reciente convertida a LocalDateTime
            LocalDateTime fechaAleatoria = faker.date()
                .past(30, TimeUnit.DAYS)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
                
            pago.setFechaPago(fechaAleatoria);

            pagoRepository.save(pago);
        }

        System.out.println("¡Se han cargado 5 pagos de prueba correctamente en pago_service!");
    }

}
