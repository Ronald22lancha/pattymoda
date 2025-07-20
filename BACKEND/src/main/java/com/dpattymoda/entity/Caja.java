package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad Caja para gestión de cajas registradoras
 */
@Entity
@Table(name = "cajas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"sucursal_id", "numero_caja"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @Column(name = "numero_caja", nullable = false, length = 20)
    private String numeroCaja;

    @Column(name = "nombre_caja", nullable = false, length = 100)
    private String nombreCaja;

    @Column(name = "terminal_pos", length = 100)
    private String terminalPos;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Builder.Default
    @Column(name = "activa")
    private Boolean activa = true;

    @Column(name = "configuracion", columnDefinition = "jsonb")
    private String configuracion;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relaciones
    @OneToMany(mappedBy = "caja", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TurnoCaja> turnos = new ArrayList<>();

    // Métodos de utilidad
    public boolean estaActiva() {
        return activa != null && activa;
    }

    public TurnoCaja getTurnoActual() {
        return turnos.stream()
                    .filter(turno -> "abierto".equals(turno.getEstado()))
                    .findFirst()
                    .orElse(null);
    }

    public boolean tieneTurnoAbierto() {
        return getTurnoActual() != null;
    }

    public String getIdentificadorCompleto() {
        return sucursal.getNombreSucursal() + " - " + nombreCaja + " (" + numeroCaja + ")";
    }
}