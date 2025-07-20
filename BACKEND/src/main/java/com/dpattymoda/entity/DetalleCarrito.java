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
 * Entidad DetalleCarrito para items del carrito
 */
@Entity
@Table(name = "detalle_carrito",
       uniqueConstraints = @UniqueConstraint(columnNames = {"carrito_id", "variante_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DetalleCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

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

    @CreatedDate
    @Column(name = "fecha_agregado", nullable = false, updatable = false)
    private LocalDateTime fechaAgregado;

    // Métodos de utilidad
    public BigDecimal getSubtotal() {
        BigDecimal precioConDescuento = precioUnitario.subtract(descuentoUnitario);
        return precioConDescuento.multiply(BigDecimal.valueOf(cantidad));
    }

    public BigDecimal getTotalDescuento() {
        return descuentoUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public String getNombreProducto() {
        return variante != null ? variante.getNombreCompleto() : "Producto eliminado";
    }

    public boolean tieneDescuento() {
        return descuentoUnitario != null && descuentoUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    public void actualizarCantidad(Integer nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = nuevaCantidad;
    }
}