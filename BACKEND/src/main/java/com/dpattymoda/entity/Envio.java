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
 * Entidad Envio para seguimiento de envíos
 */
@Entity
@Table(name = "envios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "transportista", length = 100)
    private String transportista;

    @Column(name = "numero_guia", length = 100)
    private String numeroGuia;

    @Builder.Default
    @Column(name = "estado", length = 30)
    private String estado = "preparando"; // preparando, en_camino, entregado, devuelto

    @Column(name = "fecha_despacho")
    private LocalDateTime fechaDespacho;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    @Column(name = "peso_total", precision = 8, scale = 3)
    private BigDecimal pesoTotal;

    @Column(name = "dimensiones", columnDefinition = "jsonb")
    private String dimensiones;

    @Column(name = "direccion_origen", columnDefinition = "TEXT")
    private String direccionOrigen;

    @Column(name = "direccion_destino", columnDefinition = "TEXT")
    private String direccionDestino;

    @Column(name = "instrucciones_entrega", columnDefinition = "TEXT")
    private String instruccionesEntrega;

    @Column(name = "evidencia_entrega", columnDefinition = "jsonb")
    private String evidenciaEntrega; // URLs de fotos, firma digital, etc.

    @Builder.Default
    @Column(name = "seguimiento", columnDefinition = "jsonb")
    private String seguimiento = "[]"; // Array de eventos de seguimiento

    @Builder.Default
    @Column(name = "intentos_entrega")
    private Integer intentosEntrega = 0;

    @Builder.Default
    @Column(name = "max_intentos_entrega")
    private Integer maxIntentosEntrega = 3;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Métodos de utilidad
    public boolean estaPreparando() {
        return "preparando".equals(estado);
    }

    public boolean estaEnCamino() {
        return "en_camino".equals(estado);
    }

    public boolean estaEntregado() {
        return "entregado".equals(estado);
    }

    public boolean estaDevuelto() {
        return "devuelto".equals(estado);
    }

    public void marcarComoDespacho() {
        this.estado = "en_camino";
        this.fechaDespacho = LocalDateTime.now();
    }

    public void marcarComoEntregado(String evidencia) {
        this.estado = "entregado";
        this.fechaEntregaReal = LocalDateTime.now();
        this.evidenciaEntrega = evidencia;
    }

    public void registrarIntentoFallido(String motivo) {
        this.intentosEntrega++;
        // Aquí se agregaría el evento al seguimiento
        if (this.intentosEntrega >= this.maxIntentosEntrega) {
            this.estado = "devuelto";
        }
    }

    public boolean puedeReintentar() {
        return intentosEntrega < maxIntentosEntrega && !estaEntregado() && !estaDevuelto();
    }

    public boolean estaRetrasado() {
        return fechaEntregaEstimada != null && 
               fechaEntregaEstimada.isBefore(LocalDateTime.now()) && 
               !estaEntregado();
    }

    public String getEstadoDescriptivo() {
        switch (estado) {
            case "preparando": return "Preparando envío";
            case "en_camino": return "En camino";
            case "entregado": return "Entregado";
            case "devuelto": return "Devuelto al remitente";
            default: return estado;
        }
    }
}