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
import java.util.UUID;

/**
 * Entidad DireccionEnvio para direcciones de entrega
 */
@Entity
@Table(name = "direcciones_envio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DireccionEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "alias_direccion", length = 100)
    private String aliasDireccion;

    @Column(name = "nombres_destinatario", nullable = false, length = 100)
    private String nombresDestinatario;

    @Column(name = "apellidos_destinatario", nullable = false, length = 100)
    private String apellidosDestinatario;

    @Column(name = "telefono_destinatario", length = 20)
    private String telefonoDestinatario;

    @Column(name = "direccion_linea1", nullable = false, columnDefinition = "TEXT")
    private String direccionLinea1;

    @Column(name = "direccion_linea2", columnDefinition = "TEXT")
    private String direccionLinea2;

    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @Column(name = "departamento", nullable = false, length = 100)
    private String departamento;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Builder.Default
    @Column(name = "pais", length = 100)
    private String pais = "Perú";

    @Column(name = "referencia", columnDefinition = "TEXT")
    private String referencia;

    @Column(name = "coordenadas_gps")
    private String coordenadasGps;

    @Builder.Default
    @Column(name = "es_principal")
    private Boolean esPrincipal = false;

    @Builder.Default
    @Column(name = "activa")
    private Boolean activa = true;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Métodos de utilidad
    public String getNombreCompleto() {
        return nombresDestinatario + " " + apellidosDestinatario;
    }

    public String getDireccionCompleta() {
        StringBuilder direccion = new StringBuilder(direccionLinea1);
        if (direccionLinea2 != null && !direccionLinea2.isEmpty()) {
            direccion.append(", ").append(direccionLinea2);
        }
        direccion.append(", ").append(ciudad);
        direccion.append(", ").append(departamento);
        direccion.append(", ").append(pais);
        
        if (codigoPostal != null && !codigoPostal.isEmpty()) {
            direccion.append(" ").append(codigoPostal);
        }
        
        return direccion.toString();
    }

    public String getDireccionConReferencia() {
        String direccionCompleta = getDireccionCompleta();
        if (referencia != null && !referencia.isEmpty()) {
            direccionCompleta += "\nReferencia: " + referencia;
        }
        return direccionCompleta;
    }

    public boolean estaActiva() {
        return activa != null && activa;
    }

    public boolean esDireccionPrincipal() {
        return esPrincipal != null && esPrincipal;
    }
}