package com.nativa.menu_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nativa.menu_service.dto.ProductoRequest;
import com.nativa.menu_service.dto.ProductoResponse;
import com.nativa.menu_service.exception.ResourceNotFoundException;
import com.nativa.menu_service.mapper.ProductoMapper;
import com.nativa.menu_service.repository.CategoriaRepository;
import com.nativa.menu_service.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor

@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

@Transactional(readOnly = true)
    public List<ProductoResponse> getAllDisponible() {
        log.info("Solicitando lista de productos disponibles");
        
        List<ProductoResponse> disponibles = productoRepository.findByDisponibleTrue()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
                
        log.info("Se encontraron {} productos disponibles", disponibles.size());
        return disponibles;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> getAllProductos() {
        log.info("Solicitando la lista de todos los productos registrados");
        
        List<ProductoResponse> todos = productoRepository.findAll()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
                
        log.info("Se retornaron {} productos en total", todos.size());
        return todos;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> agruparProductosDisponiblesPorCategoria(String nombre) {
        log.info("Filtrando productos disponibles por el nombre de categoría: '{}'", nombre);
        
        List<ProductoResponse> filtrados = productoRepository.findByDisponibleTrueAndCategoria_NombreIgnoreCase(nombre)
                .stream()
                .map(productoMapper::toResponse)
                .toList();
                
        log.info("Se encontraron {} productos disponibles para la categoría '{}'", filtrados.size(), nombre);
        return filtrados;    
    }

    @Transactional(readOnly = true)
    public ProductoResponse getProductoById(Long id) {
        log.info("Buscando producto con ID: {}", id);
        
        return productoRepository.findById(id)
                .map(productoMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontró el producto con ID: {}", id);
                    return new ResourceNotFoundException("Producto no encontrado");
                });
    }
    
    @Transactional
    public ProductoResponse createProducto(ProductoRequest request) {        
        log.info("Iniciando la creación de un nuevo producto: '{}' asignado a la categoría ID: {}", request.getNombre(), request.getCategoriaId());
        
        var categoria = categoriaRepository.findById(request.getCategoriaId())
            .orElseThrow(() -> {
                log.error("Error al crear producto: La categoría con ID {} no existe", request.getCategoriaId());
                return new ResourceNotFoundException("Categoría no encontrada");
            });

        var producto = productoMapper.toEntity(request);
        producto.setCategoria(categoria);
        
        var productoGuardado = productoRepository.save(producto);
        log.info("Producto creado exitosamente con ID: {}", productoGuardado.getId());
        return productoMapper.toResponse(productoGuardado);
    }

    @Transactional
    public ProductoResponse updateProducto(Long id, ProductoRequest request) {
        log.info("Iniciando actualización del producto con ID: {}", id);
        
        var producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar: El producto con ID {} no existe", id);
                    return new ResourceNotFoundException("Producto no encontrado");
                });

        var categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> {
                    log.error("Error al actualizar producto ID {}: La nueva categoría ID {} no existe", id, request.getCategoriaId());
                    return new ResourceNotFoundException("Categoría no encontrada");
                });
        
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCategoria(categoria);

        var productoActualizado = productoRepository.save(producto);
        log.info("Producto con ID: {} actualizado correctamente", id);
        return productoMapper.toResponse(productoActualizado);
    }

    @Transactional
    public void deleteProducto(Long id) {
        log.info("Iniciando desactivación (eliminación lógica) del producto con ID: {}", id);
        
        var producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al eliminar: El producto con ID {} no existe", id);
                    return new ResourceNotFoundException("Producto no encontrado");
                });

        producto.setDisponible(false);
        productoRepository.save(producto);
        
        log.info("Producto con ID: {} deshabilitado correctamente ('disponible' establecido en false)", id);
    }
}
