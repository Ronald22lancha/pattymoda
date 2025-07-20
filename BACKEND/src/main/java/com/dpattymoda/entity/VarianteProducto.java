package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad VarianteProducto para manejar tallas, colores y stock
 */
@Entity
@Table(name = "variantes_producto")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VarianteProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "talla", length = 20)
    private String talla;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "material", length = 100)
    private String material;

    @Column(name = "precio_variante", precision = 10, scale = 2)
    private BigDecimal precioVariante;

    @Column(name = "peso_variante", precision = 8, scale = 3)
    private BigDecimal pesoVariante;

    @Column(name = "imagen_variante", length = 500)
    private String imagenVariante;

    @Column(name = "codigo_barras", length = 100)
    private String codigoBarras;

    @Builder.Default
    @Column(name = "activo")
    private Boolean activo = true;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relaciones
    @OneToMany(mappedBy = "variante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Inventario> inventarios = new ArrayList<>();

    // Métodos de utilidad
    public BigDecimal getPrecioFinal() {
        return precioVariante != null ? precioVariante : producto.getPrecioVenta();
    }

    public String getNombreCompleto() {
        StringBuilder nombre = new StringBuilder(producto.getNombreProducto());
        if (talla != null) {
            nombre.append(" - Talla ").append(talla);
        }
        if (color != null) {
            nombre.append(" - ").append(color);
        }
        return nombre.toString();
    }

    public boolean estaActivo() {
        return activo != null && activo && producto.estaActivo();
    }

    public int getStockTotal() {
        return inventarios.stream()
                         .mapToInt(Inventario::getCantidadDisponible)
                         .sum();
    }

    public int getStockDisponible() {
        return inventarios.stream()
                         .mapToInt(inv -> inv.getCantidadDisponible() - inv.getCantidadReservada())
                         .sum();
    }

    public boolean tieneStock() {
        return getStockDisponible() > 0;
    }
}