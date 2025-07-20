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
import java.util.UUID;

/**
 * Entidad Pago para registro de transacciones
 */
@Entity
@Table(name = "pagos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Builder.Default
    @Column(name = "moneda", length = 10)
    private String moneda = "PEN";

    @Builder.Default
    @Column(name = "estado", nullable = false, length = 30)
    private String estado = "pendiente"; // pendiente, procesado, fallido, reembolsado

    @Column(name = "referencia_externa")
    private String referenciaExterna; // ID de transacción del proveedor de pagos

    @Column(name = "datos_transaccion", columnDefinition = "jsonb")
    private String datosTransaccion; // Respuesta completa del proveedor

    @Column(name = "fecha_procesamiento")
    private LocalDateTime fechaProcesamiento;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Builder.Default
    @Column(name = "intentos_procesamiento")
    private Integer intentosProcesamiento = 0;

    @Builder.Default
    @Column(name = "comision", precision = 10, scale = 2)
    private BigDecimal comision = BigDecimal.ZERO;

    @Column(name = "monto_neto", precision = 10, scale = 2)
    private BigDecimal montoNeto;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Métodos de utilidad
    public boolean estaPendiente() {
        return "pendiente".equals(estado);
    }

    public boolean estaProcesado() {
        return "procesado".equals(estado);
    }

    public boolean haFallado() {
        return "fallido".equals(estado);
    }

    public boolean estaReembolsado() {
        return "reembolsado".equals(estado);
    }

    public boolean esEfectivo() {
        return "efectivo".equals(metodoPago);
    }

    public boolean esTarjeta() {
        return metodoPago != null && metodoPago.toLowerCase().contains("tarjeta");
    }

    public boolean esBilleteraDigital() {
        return metodoPago != null && 
               (metodoPago.equals("yape") || metodoPago.equals("plin") || metodoPago.equals("lukita"));
    }

    public void procesarPago(String referenciaTransaccion, String datosRespuesta) {
        this.estado = "procesado";
        this.fechaProcesamiento = LocalDateTime.now();
        this.referenciaExterna = referenciaTransaccion;
        this.datosTransaccion = datosRespuesta;
        this.montoNeto = monto.subtract(comision);
    }

    public void fallarPago(String motivoFallo) {
        this.estado = "fallido";
        this.intentosProcesamiento++;
        this.notas = (this.notas != null ? this.notas + "\n" : "") + 
                    "Intento " + intentosProcesamiento + " fallido: " + motivoFallo;
    }

    public boolean puedeReintentar() {
        return haFallado() && intentosProcesamiento < 3;
    }

    public String getDescripcionMetodo() {
        switch (metodoPago.toLowerCase()) {
            case "efectivo": return "Efectivo";
            case "tarjeta_debito": return "Tarjeta de Débito";
            case "tarjeta_credito": return "Tarjeta de Crédito";
            case "yape": return "Yape";
            case "plin": return "Plin";
            case "lukita": return "Lukita";
            case "paypal": return "PayPal";
            default: return metodoPago;
        }
    }
}