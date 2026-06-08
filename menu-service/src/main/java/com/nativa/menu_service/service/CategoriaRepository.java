package com.nativa.menu_service.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
//Lombok se encarga de generar el constructor con los argumentos necesarios para inyectar las dependencias
@RequiredArgsConstructor
public class CategoriaRepository {

    private final CategoriaRepository categoriaRepository;

}
