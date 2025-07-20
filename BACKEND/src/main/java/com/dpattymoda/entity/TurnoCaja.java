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
 * Entidad TurnoCaja para control de turnos de trabajo
 */
@Entity
@Table(name = "turnos_caja")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TurnoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id", nullable = false)
    private Caja caja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cajero_id", nullable = false)
    private Usuario cajero;

    @Builder.Default
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura = LocalDateTime.now();

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Builder.Default
    @Column(name = "monto_inicial", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoInicial = BigDecimal.ZERO;

    @Column(name = "monto_final", precision = 10, scale = 2)
    private BigDecimal montoFinal;

    @Column(name = "monto_esperado", precision = 10, scale = 2)
    private BigDecimal montoEsperado;

    @Column(name = "diferencia", precision = 10, scale = 2)
    private BigDecimal diferencia;

    @Builder.Default
    @Column(name = "total_ventas_efectivo", precision = 10, scale = 2)
    private BigDecimal totalVentasEfectivo = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_ventas_tarjeta", precision = 10, scale = 2)
    private BigDecimal totalVentasTarjeta = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_ventas_digital", precision = 10, scale = 2)
    private BigDecimal totalVentasDigital = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_egresos", precision = 10, scale = 2)
    private BigDecimal totalEgresos = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "numero_transacciones")
    private Integer numeroTransacciones = 0;

    @Builder.Default
    @Column(name = "estado", length = 20)
    private String estado = "abierto"; // abierto, cerrado, cuadrado

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Usuario supervisor;

    @Column(name = "fecha_supervision")
    private LocalDateTime fechaSupervision;

    @Column(name = "arqueo_detalle", columnDefinition = "jsonb")
    private String arqueoDetalle;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relaciones
    @OneToMany(mappedBy = "turnoCaja", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MovimientoCaja> movimientos = new ArrayList<>();

    // Métodos de utilidad
    public boolean estaAbierto() {
        return "abierto".equals(estado) && fechaCierre == null;
    }

    public boolean estaCerrado() {
        return "cerrado".equals(estado) || fechaCierre != null;
    }

    public BigDecimal getTotalVentas() {
        return totalVentasEfectivo.add(totalVentasTarjeta).add(totalVentasDigital);
    }

    public BigDecimal getMontoEnCaja() {
        return montoInicial.add(totalVentasEfectivo).subtract(totalEgresos);
    }

    public void cerrarTurno(BigDecimal montoContado, String observacionesCierre) {
        this.fechaCierre = LocalDateTime.now();
        this.montoFinal = montoContado;
        this.montoEsperado = getMontoEnCaja();
        this.diferencia = montoFinal.subtract(montoEsperado);
        this.estado = "cerrado";
        if (observacionesCierre != null) {
            this.observaciones = (this.observaciones != null ? this.observaciones + "\n" : "") + observacionesCierre;
        }
    }

    public boolean tieneDiferencia() {
        return diferencia != null && diferencia.compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean estaDescuadrado() {
        return tieneDiferencia() && diferencia.abs().compareTo(new BigDecimal("1.00")) > 0;
    }

    public String getDuracionTurno() {
        if (fechaCierre == null) {
            return "En curso";
        }
        
        long horas = java.time.Duration.between(fechaApertura, fechaCierre).toHours();
        long minutos = java.time.Duration.between(fechaApertura, fechaCierre).toMinutes() % 60;
        
        return String.format("%d horas y %d minutos", horas, minutos);
    }
}