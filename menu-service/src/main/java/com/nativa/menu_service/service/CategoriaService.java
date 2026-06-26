package com.nativa.menu_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nativa.menu_service.dto.CategoriaRequest;
import com.nativa.menu_service.dto.CategoriaResponse;
import com.nativa.menu_service.exception.ResourceNotFoundException;
import com.nativa.menu_service.mapper.CategoriaMapper;
import com.nativa.menu_service.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
@Slf4j
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    
@Transactional(readOnly = true)
    public List<CategoriaResponse> getCategoriasDisponible() {
        log.info("Solicitando lista de categorías disponibles");
        
        List<CategoriaResponse> disponibles = categoriaRepository.findByDisponibleTrue()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
                
        log.info("Se encontraron {} categorías disponibles", disponibles.size());
        return disponibles;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> getAllCategorias() {
        log.info("Solicitando la lista de todas las categorías registradas");
        
        List<CategoriaResponse> todas = categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
                
        log.info("Se retornaron {} categorías en total", todas.size());
        return todas;
    }

    @Transactional(readOnly = true)
    public CategoriaResponse getCategoriaById(Long id) {
        log.info("Buscando categoría con ID: {}", id);
        
        return categoriaRepository.findById(id)
                .map(categoriaMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontró la categoría con ID: {}", id);
                    return new ResourceNotFoundException("Categoría no encontrada");
                });
    }

    @Transactional
    public CategoriaResponse createCategoria(CategoriaRequest request) {
        log.info("Iniciando la creación de una nueva categoría con nombre: '{}'", request.getNombre());
        
        var categoria = categoriaMapper.toEntity(request);
        var categoriaGuardada = categoriaRepository.save(categoria);
        
        log.info("Categoría creada exitosamente con ID: {}", categoriaGuardada.getId());
        return categoriaMapper.toResponse(categoriaGuardada);
    }

    @Transactional
    public CategoriaResponse updateCategoria(Long id, CategoriaRequest request) {
        log.info("Iniciando actualización de la categoría con ID: {}", id);
        
        var categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar: La categoría con ID {} no existe", id);
                    return new ResourceNotFoundException("Categoría no encontrada");
                });

        categoria.setNombre(request.getNombre());
        var categoriaActualizada = categoriaRepository.save(categoria);
        
        log.info("Categoría con ID: {} actualizada correctamente a nombre: '{}'", id, request.getNombre());
        
        // --- AQUÍ ESTABA EL ERROR (Faltaban estas dos líneas de abajo) ---
        return categoriaMapper.toResponse(categoriaActualizada);
    }

    @Transactional
    public void deleteCategoria(Long id) {
        log.info("Iniciando desactivación (eliminación lógica) de la categoría con ID: {}", id);
        
        var categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al eliminar: La categoría con ID {} no existe", id);
                    return new ResourceNotFoundException("Categoría no encontrada");
                });

        categoria.setDisponible(false);
        categoriaRepository.save(categoria);
        
        log.info("Categoría con ID: {} deshabilitada correctamente ('disponible' establecido en false)", id);
    }
}
