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
 * Entidad Sucursal para manejo de múltiples tiendas
 */
@Entity
@Table(name = "sucursales")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nombre_sucursal", nullable = false, length = 100)
    private String nombreSucursal;

    @Column(name = "direccion", nullable = false, columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "horario_atencion", columnDefinition = "jsonb")
    private String horarioAtencion;

    @Column(name = "coordenadas_gps")
    private String coordenadasGps;

    @Builder.Default
    @Column(name = "activa")
    private Boolean activa = true;

    @Builder.Default
    @Column(name = "es_principal")
    private Boolean esPrincipal = false;

    @Column(name = "configuracion", columnDefinition = "jsonb")
    private String configuracion;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relaciones
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Inventario> inventarios = new ArrayList<>();

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Caja> cajas = new ArrayList<>();

    // Métodos de utilidad
    public boolean estaActiva() {
        return activa != null && activa;
    }

    public boolean esSucursalPrincipal() {
        return esPrincipal != null && esPrincipal;
    }

    public String getDireccionCompleta() {
        return direccion + (telefono != null ? " - Tel: " + telefono : "");
    }
}