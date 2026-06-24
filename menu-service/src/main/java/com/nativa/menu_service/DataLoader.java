package com.nativa.menu_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nativa.menu_service.model.Categoria;
import com.nativa.menu_service.model.Producto;
import com.nativa.menu_service.repository.CategoriaRepository;
import com.nativa.menu_service.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
 
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
 
    @Override
    public void run(String... args) throws Exception {
 
        // Liquibase ya sembró las 8 categorías (changeset:003).
        // Si ya hay productos, este DataLoader ya corrió antes → salir.
        if (productoRepository.count() > 0) {
            System.out.println("DataLoader: productos ya cargados. Omitiendo...");
            return;
        }
 
        // Recuperamos las categorías creadas por Liquibase, indexadas por nombre
        Map<String, Categoria> cat = categoriaRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Categoria::getNombre, c -> c));
 
        productoRepository.saveAll(List.of(
            build("Empanadas de Pino",    "Dos empanadas horneadas con pino de carne, cebolla, huevo y aceituna", 4500,  cat.get("Entradas")),
            build("Ceviche de Reineta",   "Reineta fresca marinada en limón con cebolla, cilantro y ají verde",    6500,  cat.get("Entradas")),
            build("Lomo a lo Pobre",      "Bife de lomo con papas fritas, cebolla frita y dos huevos fritos",       12900, cat.get("Platos de Fondo")),
            build("Pastel de Choclo",     "Pastel al horno en paila de greda con pino de carne y pollo",            9500,  cat.get("Platos de Fondo")),
            build("Papas Fritas",         "Porción grande de papas fritas rústicas crujientes",                      5500,  cat.get("Acompañamientos")),
            build("Arroz Chaufa",         "Porción de arroz saltado con cebollín y pimentón",                        3200,  cat.get("Acompañamientos")),
            build("Mote con Huesillo",    "Postre tradicional con dos huesillos y mote de trigo",                    3800,  cat.get("Postres")),
            build("Torta de Milhojas",    "Porción de milhojas con crema pastelera y manjar",                        3200,  cat.get("Postres")),
            build("Bebida en Lata 350cc", "Coca-Cola, Sprite o Fanta helada",                                        1800,  cat.get("Bebestibles")),
            build("Pisco Sour Catedral",  "Receta de la casa con pisco peruano y limón sutil",                       5200,  cat.get("Bebestibles")),
            build("Café Americano",       "Café de grano molido servido en taza grande",                              2200,  cat.get("Cafetería")),
            build("Cappuccino",           "Espresso doble con leche vaporizada y espuma densa",                       3000,  cat.get("Cafetería")),
            build("Churrasco Italiano",   "Láminas de carne, palta, tomate y mayonesa casera en pan frica",           7900,  cat.get("Sánguches")),
            build("Sánguche de Pernil",   "Pernil de cerdo en marraqueta con pebre y mayonesa",                       6500,  cat.get("Sánguches")),
            build("Porción de Palta",     "Agregado de palta molida para tus platos o sánguches",                    1500,  cat.get("Agregados"))
        ));
 
        System.out.println("DataLoader: " + productoRepository.count() + " productos cargados correctamente en menu-service.");
    }
 
    private Producto build(String nombre, String descripcion, int precio, Categoria categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(BigDecimal.valueOf(precio));
        p.setDisponible(true);
        p.setCategoria(categoria);
        return p;
    }
}