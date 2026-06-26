package com.nativa.pedido_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.nativa.pedido_service.model.DetallePedido;
import com.nativa.pedido_service.model.Pedido;
import com.nativa.pedido_service.repository.DetallePedidoRepository;
import com.nativa.pedido_service.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
 
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
 
    @Override
    public void run(String... args) throws Exception {
 
        // Si ya hay detalles cargados, omitimos para evitar duplicados
        if (detallePedidoRepository.count() > 0) {
            System.out.println("DataLoader: detalles_pedido ya cargados. Omitiendo...");
            return;
        }
 
        // 1. Recuperamos todos los pedidos de la base de datos (sembrados por Liquibase)
        // y los indexamos en un Map usando su ID como clave.
        Map<Long, Pedido> pedidosMap = pedidoRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Pedido::getId, pedido -> pedido));
 
        // 2. Sembramos los detalles utilizando los datos extraídos del changeset
        detallePedidoRepository.saveAll(List.of(
            // build(productoId, cantidad, precioUnitario, subtotal, pedido)
            build(10L, 2, 5200,  10400, pedidosMap.get(1L)),
            build(2L,  3, 6500,  19500, pedidosMap.get(2L)),
            build(5L,  3, 5500,  16500, pedidosMap.get(3L)),
            build(6L,  2, 3200,  6400, pedidosMap.get(4L)),
            build(4L,  4, 9500,  38000, pedidosMap.get(5L)),
            build(5L,  2, 5500, 11000, pedidosMap.get(6L)),
            build(6L,  5, 3200,  16000, pedidosMap.get(7L)),
            build(7L,  3, 3800,  11400, pedidosMap.get(8L)),
            build(9L,  3, 1800, 5400, pedidosMap.get(9L)),
            build(3L,  4, 12900,  51600, pedidosMap.get(10L))
        ));
 
        System.out.println("DataLoader: " + detallePedidoRepository.count() + " detalles de pedido cargados correctamente.");
    }
 
    private DetallePedido build(Long productoId, int cantidad, double precioUnitario, double subtotal, Pedido pedido) {
        // Validación por seguridad en caso de que un ID de pedido no exista en la BD
        if (pedido == null) {
            throw new IllegalArgumentException("No se encontró el Pedido en la base de datos para asociar el detalle.");
        }

        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido); // Asigna el objeto Pedido completo (cumple con @ManyToOne)
        detalle.setProductoId(productoId);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(BigDecimal.valueOf(precioUnitario));
        detalle.setSubtotal(BigDecimal.valueOf(subtotal));
        
        return detalle;
    }
}