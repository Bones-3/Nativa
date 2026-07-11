package com.example.inventario.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.inventario.dto.InventarioRequest;
import com.example.inventario.dto.InventarioResponse;
import com.example.inventario.model.Inventario;

@Component
public class InventarioMapper {

    public Inventario toEntity(InventarioRequest request) {

        Inventario inventario = new Inventario();

        inventario.setProductoId(request.getProductoId());
        inventario.setNombreProducto(request.getNombreProducto());
        inventario.setStockActual(request.getStockActual());
        inventario.setStockMinimo(request.getStockMinimo());
        inventario.setUnidadMedida(request.getUnidadMedida());
        inventario.setUltimaActualizacion(LocalDateTime.now());

        return inventario;
    }

    public InventarioResponse toResponse(Inventario inventario) {
        return new InventarioResponse(
                inventario.getId(),
                inventario.getProductoId(),
                inventario.getNombreProducto(),
                inventario.getStockActual(),
                inventario.getStockMinimo(),
                inventario.getUnidadMedida()
        );
    }
}
