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
 * Entidad MovimientoCaja para registro de transacciones
 */
@Entity
@Table(name = "movimientos_caja")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MovimientoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_caja_id", nullable = false)
    private TurnoCaja turnoCaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "tipo_movimiento", nullable = false, length = 30)
    private String tipoMovimiento; // venta, devolucion, gasto, retiro, ingreso_extra

    @Column(name = "concepto", nullable = false, length = 200)
    private String concepto;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago; // efectivo, tarjeta, yape, plin, etc.

    @Column(name = "referencia", length = 100)
    private String referencia; // Número de operación, voucher, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autorizado_por")
    private Usuario autorizadoPor;

    @Column(name = "comprobante_url", length = 500)
    private String comprobanteUrl;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Builder.Default
    @Column(name = "fecha_movimiento")
    private LocalDateTime fechaMovimiento = LocalDateTime.now();

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Métodos de utilidad
    public boolean esIngreso() {
        return "venta".equals(tipoMovimiento) || "ingreso_extra".equals(tipoMovimiento);
    }

    public boolean esEgreso() {
        return "devolucion".equals(tipoMovimiento) || "gasto".equals(tipoMovimiento) || "retiro".equals(tipoMovimiento);
    }

    public boolean esEfectivo() {
        return "efectivo".equals(metodoPago);
    }

    public String getDescripcionCompleta() {
        return concepto + (referencia != null ? " - Ref: " + referencia : "");
    }
}