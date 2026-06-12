package com.nativa.menu_service.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nativa.menu_service.dto.ProductoRequest;
import com.nativa.menu_service.dto.ProductoResponse;
import com.nativa.menu_service.mapper.ProductoMapper;
import com.nativa.menu_service.repository.CategoriaRepository;
import com.nativa.menu_service.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @Transactional(readOnly = true)
    public List<ProductoResponse> getAllDisponible() {
        return productoRepository.findByDisponibleTrue()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> getAllProductos() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> agruparProductosDisponiblesPorCategoria(String nombre) {
        return productoRepository.findByDisponibleTrueAndCategoria_NombreIgnoreCase(nombre)
                .stream()
                .map(productoMapper::toResponse)
                .toList();    
    }

    @Transactional(readOnly = true)
    public ProductoResponse getProductoById(Long id) {
        return productoRepository.findById(id)
                .map(productoMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
    
    @Transactional
    public ProductoResponse createProducto(ProductoRequest request) {        
        var categoria = categoriaRepository.findById(request.getCategoriaId())
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        var producto = productoMapper.toEntity(request);
        producto.setCategoria(categoria);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse updateProducto(Long id, ProductoRequest request) {
        var producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        var categoria = categoriaRepository.findById(request.getCategoriaId())
        .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCategoria(categoria);

        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void deleteProducto(Long id) {
        var producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setDisponible(false);
        productoRepository.save(producto);
    }
}
