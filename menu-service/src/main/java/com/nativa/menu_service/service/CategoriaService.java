package com.nativa.menu_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nativa.menu_service.dto.CategoriaRequest;
import com.nativa.menu_service.dto.CategoriaResponse;
import com.nativa.menu_service.mapper.CategoriaMapper;
import com.nativa.menu_service.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    
    public List <CategoriaResponse> getAllCategorias() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    public CategoriaResponse getCategoriaById(Long id) {
        return categoriaRepository.findById(id)
                .map(categoriaMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    public CategoriaResponse createCategoria(CategoriaRequest request) {
        var categoria = categoriaMapper.toEntity(request);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    public CategoriaResponse updateCategoria(Long id, CategoriaRequest request) {
        var categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    public void deleteCategoria(Long id) {
        var categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoria.setDisponible(false);
        categoriaRepository.save(categoria);
    }
}
