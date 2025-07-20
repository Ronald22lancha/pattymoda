package com.dpattymoda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Usuario para el sistema DPattyModa
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "dni", length = 20)
    private String dni;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "genero", length = 20)
    private String genero;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @Builder.Default
    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Builder.Default
    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "token_verificacion")
    private String tokenVerificacion;

    @Builder.Default
    @Column(name = "email_verificado")
    private Boolean emailVerificado = false;

    @Column(name = "token_recuperacion")
    private String tokenRecuperacion;

    @Column(name = "fecha_token_recuperacion")
    private LocalDateTime fechaTokenRecuperacion;

    @Column(name = "preferencias", columnDefinition = "jsonb")
    private String preferencias;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Métodos de utilidad
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public boolean estaActivo() {
        return activo != null && activo;
    }

    public boolean estaBloqueado() {
        return bloqueadoHasta != null && bloqueadoHasta.isAfter(LocalDateTime.now());
    }

    public boolean esAdministrador() {
        return rol != null && "Administrador".equals(rol.getNombreRol());
    }

    public boolean esCajero() {
        return rol != null && ("Cajero".equals(rol.getNombreRol()) || esAdministrador());
    }

    public boolean esEmpleado() {
        return rol != null && ("Empleado".equals(rol.getNombreRol()) || esCajero() || esAdministrador());
    }

    public boolean esCliente() {
        return rol != null && "Cliente".equals(rol.getNombreRol());
    }
}