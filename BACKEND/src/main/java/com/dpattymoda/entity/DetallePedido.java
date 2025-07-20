package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad DetallePedido para items de cada pedido
 */
@Entity
@Table(name = "detalle_pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private VarianteProducto variante;

    @Builder.Default
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 1;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Builder.Default
    @Column(name = "descuento_unitario", precision = 10, scale = 2)
    private BigDecimal descuentoUnitario = BigDecimal.ZERO;

    @Column(name = "datos_producto", columnDefinition = "jsonb")
    private String datosProducto; // Snapshot del producto al momento de la venta

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Métodos de utilidad
    public BigDecimal getSubtotal() {
        BigDecimal precioConDescuento = precioUnitario.subtract(descuentoUnitario);
        return precioConDescuento.multiply(BigDecimal.valueOf(cantidad));
    }

    public BigDecimal getTotalDescuento() {
        return descuentoUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public BigDecimal getPrecioFinalUnitario() {
        return precioUnitario.subtract(descuentoUnitario);
    }

    public String getNombreProducto() {
        return variante != null ? variante.getNombreCompleto() : "Producto eliminado";
    }

    public boolean tieneDescuento() {
        return descuentoUnitario != null && descuentoUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getPorcentajeDescuento() {
        if (!tieneDescuento()) {
            return BigDecimal.ZERO;
        }
        return descuentoUnitario.divide(precioUnitario, 4, BigDecimal.ROUND_HALF_UP)
                               .multiply(BigDecimal.valueOf(100));
    }
}