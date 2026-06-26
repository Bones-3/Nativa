package com.nativa.reserva_service;

import com.nativa.reserva_service.model.Mesa;
import com.nativa.reserva_service.model.Reserva;
import com.nativa.reserva_service.repository.MesaRepository;
import com.nativa.reserva_service.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;

    @Override
    public void run(String... args) {
        if (reservaRepository.count() > 0) {
            System.out.println("DataLoader: reservas ya cargadas. Omitiendo...");
            return;
        }

        Map<Integer, Mesa> mesas = mesaRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Mesa::getNumero, m -> m));

        reservaRepository.saveAll(List.of(
                build("Juan Pérez",       "+56 9 1234 5678", "juan@email.com",    LocalDate.of(2026, 7, 15), LocalTime.of(20, 0),  4, "PENDIENTE",  mesas.get(3)),
                build("María González",   "+56 9 2345 6789", "maria@email.com",   LocalDate.of(2026, 7, 15), LocalTime.of(21, 30), 2, "PENDIENTE",  mesas.get(6)),
                build("Carlos Muñoz",     "+56 9 3456 7890", "carlos@email.com",  LocalDate.of(2026, 7, 16), LocalTime.of(19, 0),  6, "CONFIRMADA", mesas.get(4)),
                build("Ana Soto",         "+56 9 4567 8901", "ana@email.com",     LocalDate.of(2026, 7, 16), LocalTime.of(13, 0),  2, "PENDIENTE",  mesas.get(9)),
                build("Pedro Rojas",      "+56 9 5678 9012", "pedro@email.com",   LocalDate.of(2026, 7, 17), LocalTime.of(21, 0),  8, "CANCELADA",  mesas.get(5))
        ));

        System.out.println("DataLoader: " + reservaRepository.count() + " reservas cargadas correctamente en reserva-service.");
    }

    private Reserva build(String nombre, String telefono, String email, LocalDate fecha, LocalTime hora,
                        int personas, String estado, Mesa mesa) {
        Reserva r = new Reserva();
        r.setNombreCliente(nombre);
        r.setTelefono(telefono);
        r.setEmail(email);
        r.setFecha(fecha);
        r.setHora(hora);
        r.setCantidadPersonas(personas);
        r.setEstado(estado);
        r.setFechaCreacion(LocalDateTime.now());
        r.setMesa(mesa);
        return r;
    }
}
