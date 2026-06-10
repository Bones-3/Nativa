package com.nativa.pedido_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedidos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Column(name = "tipo_entrega")
    private String tipoEntrega;

    @Column(name = "total_pagar")
    private BigDecimal totalPagar;

    @Column(name = "tipo_pago") 
    private String tipoPago;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles;
}