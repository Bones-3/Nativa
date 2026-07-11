package com.nativa.promocion_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.promocion_service.dto.PromocionRequest;
import com.nativa.promocion_service.dto.PromocionResponse;
import com.nativa.promocion_service.model.Promocion;

@Component
public class PromocionMapper {

    public Promocion toEntity(PromocionRequest request) {

        Promocion promocion = new Promocion();

        promocion.setCodigo(request.getCodigo());
        promocion.setDescripcion(request.getDescripcion());
        promocion.setPorcentajeDescuento(request.getPorcentajeDescuento());
        promocion.setFechaInicio(request.getFechaInicio());
        promocion.setFechaFin(request.getFechaFin());
        promocion.setActivo(request.getActivo());

        return promocion;
    }

    public PromocionResponse toResponse(Promocion promocion) {

        return PromocionResponse.builder()
                .id(promocion.getId())
                .codigo(promocion.getCodigo())
                .descripcion(promocion.getDescripcion())
                .porcentajeDescuento(promocion.getPorcentajeDescuento())
                .fechaInicio(promocion.getFechaInicio())
                .fechaFin(promocion.getFechaFin())
                .activo(promocion.getActivo())
                .build();
    }
}
