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

@Service
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    
    @Transactional(readOnly = true)
    public List <CategoriaResponse> getCategoriasDisponible() {
        return categoriaRepository.findByDisponibleTrue()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List <CategoriaResponse> getAllCategorias() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse getCategoriaById(Long id) {
        return categoriaRepository.findById(id)
                .map(categoriaMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    @Transactional
    public CategoriaResponse createCategoria(CategoriaRequest request) {
        var categoria = categoriaMapper.toEntity(request);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse updateCategoria(Long id, CategoriaRequest request) {
        var categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        categoria.setNombre(request.getNombre());

        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void deleteCategoria(Long id) {
        var categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        categoria.setDisponible(false);
        categoriaRepository.save(categoria);
    }
}
