package com.nativa.horario_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.horario_service.dto.HorarioRequest;
import com.nativa.horario_service.dto.HorarioResponse;
import com.nativa.horario_service.model.Horario;



@Component
public class HorarioMapper {

    public Horario toEntity(HorarioRequest request) {

        Horario horario = new Horario();

        horario.setDiaSemana(request.getDiaSemana());
        horario.setHoraApertura(request.getHoraApertura());
        horario.setHoraCierre(request.getHoraCierre());

        return horario;
    }

    public HorarioResponse toResponse(Horario horario) {

        return HorarioResponse.builder()
                .id(horario.getId())
                .diaSemana(horario.getDiaSemana())
                .horaApertura(horario.getHoraApertura())
                .horaCierre(horario.getHoraCierre())
                .cerrado(horario.getCerrado())
                .build();
    }
}
