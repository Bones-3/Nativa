package com.nativa.horario_service;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nativa.horario_service.model.Horario;
import com.nativa.horario_service.repository.HorarioRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final HorarioRepository horarioRepository;

@Override
public void run(String... args) throws Exception {
    if (horarioRepository.count() >= 7) {
        System.out.println("DataLoader ya fue ejecutado anteriormente. Omitiendo...");
        return;
    }

    String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    LocalTime apertura = LocalTime.of(8, 0);
    LocalTime cierre = LocalTime.of(22, 0);

    for (String dia : dias) {
        Horario horario = new Horario();
        horario.setDiaSemana(dia);
        horario.setHoraApertura(apertura);
        horario.setHoraCierre(cierre);
        horario.setCerrado(dia.equals("Domingo"));
        horarioRepository.save(horario);
    }

    System.out.println("Se han cargado 7 horarios de prueba correctamente en horario-service!");
}
}
