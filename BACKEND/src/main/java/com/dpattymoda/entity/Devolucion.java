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
 * Entidad Devolucion para gestión de devoluciones y cambios
 */
@Entity
@Table(name = "devoluciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "numero_devolucion", nullable = false, unique = true, length = 50)
    private String numeroDevolucion;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo; // devolucion, cambio

    @Column(name = "motivo", nullable = false, length = 100)
    private String motivo;

    @Column(name = "descripcion_detallada", columnDefinition = "TEXT")
    private String descripcionDetallada;

    @Builder.Default
    @Column(name = "estado", length = 30)
    private String estado = "solicitada"; // solicitada, aprobada, rechazada, procesando, completada

    @Column(name = "items_devolucion", nullable = false, columnDefinition = "jsonb")
    private String itemsDevolucion; // Array de items con cantidades

    @Column(name = "monto_devolucion", precision = 10, scale = 2)
    private BigDecimal montoDevolucion;

    @Column(name = "metodo_reembolso", length = 50)
    private String metodoReembolso;

    @CreatedDate
    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "fecha_recepcion_items")
    private LocalDateTime fechaRecepcionItems;

    @Column(name = "fecha_completada")
    private LocalDateTime fechaCompletada;

    @Column(name = "evidencia_fotos", columnDefinition = "jsonb")
    private String evidenciaFotos; // URLs de fotos del estado del producto

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por")
    private Usuario aprobadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procesado_por")
    private Usuario procesadoPor;

    @Column(name = "notas_cliente", columnDefinition = "TEXT")
    private String notasCliente;

    @Column(name = "notas_internas", columnDefinition = "TEXT")
    private String notasInternas;

    @Column(name = "costo_envio_devolucion", precision = 10, scale = 2)
    private BigDecimal costoEnvioDevolucion;

    @Column(name = "numero_guia_devolucion", length = 100)
    private String numeroGuiaDevolucion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Métodos de utilidad
    public boolean estaSolicitada() {
        return "solicitada".equals(estado);
    }

    public boolean estaAprobada() {
        return "aprobada".equals(estado);
    }

    public boolean estaRechazada() {
        return "rechazada".equals(estado);
    }

    public boolean estaCompletada() {
        return "completada".equals(estado);
    }

    public boolean esDevolucion() {
        return "devolucion".equals(tipo);
    }

    public boolean esCambio() {
        return "cambio".equals(tipo);
    }

    public void aprobar(Usuario empleado, String observaciones) {
        this.estado = "aprobada";
        this.fechaAprobacion = LocalDateTime.now();
        this.aprobadoPor = empleado;
        if (observaciones != null) {
            this.notasInternas = (this.notasInternas != null ? this.notasInternas + "\n" : "") + observaciones;
        }
    }

    public void rechazar(Usuario empleado, String motivo) {
        this.estado = "rechazada";
        this.fechaAprobacion = LocalDateTime.now();
        this.aprobadoPor = empleado;
        this.notasInternas = (this.notasInternas != null ? this.notasInternas + "\n" : "") + "Rechazada: " + motivo;
    }

    public void completar(String metodoReembolsoFinal) {
        this.estado = "completada";
        this.fechaCompletada = LocalDateTime.now();
        this.metodoReembolso = metodoReembolsoFinal;
    }

    public String getDescripcionCompleta() {
        return motivo + (descripcionDetallada != null ? " - " + descripcionDetallada : "");
    }

    public boolean puedeSerProcesada() {
        return estaAprobada() && fechaRecepcionItems == null;
    }
}