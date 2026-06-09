package com.nativa.menu_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.menu_service.dto.ProductoRequest;
import com.nativa.menu_service.dto.ProductoResponse;
import com.nativa.menu_service.model.Producto;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequest request) {

        Producto producto = new Producto();

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());

        return producto;
    }

    public ProductoResponse toResponse(Producto producto) {

        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .disponible(producto.getDisponible())
                .build();
    }
}
