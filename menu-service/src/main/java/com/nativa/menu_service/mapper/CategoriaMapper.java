package com.nativa.menu_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.menu_service.dto.CategoriaRequest;
import com.nativa.menu_service.dto.CategoriaResponse;
import com.nativa.menu_service.model.Categoria;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequest request) {

        Categoria categoria = new Categoria();

        categoria.setNombre(request.getNombre());
        categoria.setDisponible(true);
        
        return categoria;   
    }

    public CategoriaResponse toResponse(Categoria categoria) {

        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .disponible(categoria.getDisponible())
                .build();
    }
}
