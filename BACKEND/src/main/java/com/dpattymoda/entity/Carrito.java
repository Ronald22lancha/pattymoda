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
 * Entidad Carrito para compras online
 */
@Entity
@Table(name = "carritos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "sesion_id")
    private String sesionId;

    @Builder.Default
    @Column(name = "estado", length = 20)
    private String estado = "activo"; // activo, convertido, abandonado

    @Builder.Default
    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "impuestos", precision = 10, scale = 2)
    private BigDecimal impuestos = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "cupones_aplicados", columnDefinition = "jsonb")
    private String cuponesAplicados;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion = LocalDateTime.now().plusDays(30);

    // Relaciones
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetalleCarrito> detalles = new ArrayList<>();

    // Métodos de utilidad
    public boolean estaActivo() {
        return "activo".equals(estado);
    }

    public boolean estaExpirado() {
        return fechaExpiracion != null && fechaExpiracion.isBefore(LocalDateTime.now());
    }

    public int getCantidadTotalItems() {
        return detalles.stream()
                      .mapToInt(DetalleCarrito::getCantidad)
                      .sum();
    }

    public void calcularTotales() {
        this.subtotal = detalles.stream()
                              .map(DetalleCarrito::getSubtotal)
                              .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // IGV (18% en Perú)
        this.impuestos = subtotal.subtract(descuento)
                               .multiply(new BigDecimal("0.18"));
        
        this.total = subtotal.subtract(descuento)
                           .add(impuestos)
                           .add(costoEnvio);
    }

    public void convertirAPedido() {
        this.estado = "convertido";
    }

    public void marcarComoAbandonado() {
        this.estado = "abandonado";
    }
}