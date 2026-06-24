package com.nativa.resena_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.resena_service.dto.ResenaRequest;
import com.nativa.resena_service.dto.ResenaResponse;
import com.nativa.resena_service.model.Resena;

@Component
public class ResenaMapper {

    public Resena toEntity(ResenaRequest request) {

        Resena resena = new Resena();

        resena.setProductoId(request.getProductoId());
        resena.setUsuarioId(request.getUsuarioId());
        resena.setComentario(request.getComentario());
        resena.setCalificacion(request.getCalificacion());

        return resena;
    }

    public ResenaResponse toResponse(Resena resena) {

        return ResenaResponse.builder()
                .id(resena.getId())
                .productoId(resena.getProductoId())
                .usuarioId(resena.getUsuarioId())
                .comentario(resena.getComentario())
                .calificacion(resena.getCalificacion())
                .build();
    }
}
