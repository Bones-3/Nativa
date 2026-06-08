package com.nativa.menu_service.service;

import org.springframework.stereotype.Service;

import com.nativa.menu_service.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    
}
