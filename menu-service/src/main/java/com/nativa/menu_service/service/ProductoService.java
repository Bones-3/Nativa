package com.nativa.menu_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nativa.menu_service.dto.ProductoRequest;
import com.nativa.menu_service.dto.ProductoResponse;
import com.nativa.menu_service.mapper.ProductoMapper;
import com.nativa.menu_service.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    public List<ProductoResponse> getAllProductos() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    public ProductoResponse getProductoById(Long id) {
        return productoRepository.findById(id)
                .map(productoMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
    
    public ProductoResponse createProducto(ProductoRequest request) {
        var producto = productoMapper.toEntity(request);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    public ProductoResponse updateProducto(Long id, ProductoRequest request) {
        var producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());

        return productoMapper.toResponse(productoRepository.save(producto));
    }

    public void deleteProducto(Long id) {
        var producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setDisponible(false);
        productoRepository.save(producto);
    }
}
