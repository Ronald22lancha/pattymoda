package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Inventario para control de stock por variante y sucursal
 */
@Entity
@Table(name = "inventario", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"variante_id", "sucursal_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private VarianteProducto variante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @Builder.Default
    @Column(name = "cantidad_disponible")
    private Integer cantidadDisponible = 0;

    @Builder.Default
    @Column(name = "cantidad_reservada")
    private Integer cantidadReservada = 0;

    @Builder.Default
    @Column(name = "cantidad_minima")
    private Integer cantidadMinima = 5;

    @Builder.Default
    @Column(name = "cantidad_maxima")
    private Integer cantidadMaxima = 1000;

    @Column(name = "ubicacion_fisica", length = 100)
    private String ubicacionFisica;

    @Builder.Default
    @Column(name = "ultimo_movimiento")
    private LocalDateTime ultimoMovimiento = LocalDateTime.now();

    @Column(name = "fecha_ultimo_ingreso")
    private LocalDateTime fechaUltimoIngreso;

    @Column(name = "fecha_ultimo_egreso")
    private LocalDateTime fechaUltimoEgreso;

    @Column(name = "costo_promedio", precision = 10, scale = 2)
    private BigDecimal costoPromedio;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Métodos de utilidad
    public Integer getCantidadReal() {
        return cantidadDisponible - cantidadReservada;
    }

    public boolean necesitaReposicion() {
        return cantidadDisponible <= cantidadMinima;
    }

    public boolean estaEnExceso() {
        return cantidadDisponible >= cantidadMaxima;
    }

    public boolean puedeReservar(Integer cantidad) {
        return getCantidadReal() >= cantidad;
    }

    public void reservarStock(Integer cantidad) {
        if (puedeReservar(cantidad)) {
            this.cantidadReservada += cantidad;
            this.ultimoMovimiento = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Stock insuficiente para reservar");
        }
    }

    public void liberarStock(Integer cantidad) {
        this.cantidadReservada = Math.max(0, this.cantidadReservada - cantidad);
        this.ultimoMovimiento = LocalDateTime.now();
    }

    public void confirmarVenta(Integer cantidad) {
        this.cantidadReservada = Math.max(0, this.cantidadReservada - cantidad);
        this.cantidadDisponible = Math.max(0, this.cantidadDisponible - cantidad);
        this.fechaUltimoEgreso = LocalDateTime.now();
        this.ultimoMovimiento = LocalDateTime.now();
    }

    public void ingresarStock(Integer cantidad, BigDecimal costoUnitario) {
        // Calcular nuevo costo promedio ponderado
        if (costoUnitario != null && costoUnitario.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal valorActual = costoPromedio != null ? 
                costoPromedio.multiply(BigDecimal.valueOf(cantidadDisponible)) : BigDecimal.ZERO;
            BigDecimal valorNuevo = costoUnitario.multiply(BigDecimal.valueOf(cantidad));
            BigDecimal cantidadTotal = BigDecimal.valueOf(cantidadDisponible + cantidad);
            
            this.costoPromedio = valorActual.add(valorNuevo).divide(cantidadTotal, 2, BigDecimal.ROUND_HALF_UP);
        }
        
        this.cantidadDisponible += cantidad;
        this.fechaUltimoIngreso = LocalDateTime.now();
        this.ultimoMovimiento = LocalDateTime.now();
    }
}