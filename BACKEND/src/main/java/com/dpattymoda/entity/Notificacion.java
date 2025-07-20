package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Notificacion para sistema de notificaciones
 */
@Entity
@Table(name = "notificaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "datos", columnDefinition = "jsonb")
    private String datos;

    @Column(name = "canal", nullable = false, length = 20)
    private String canal; // email, push, sms, in_app

    @Builder.Default
    @Column(name = "leida")
    private Boolean leida = false;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    @Builder.Default
    @Column(name = "enviada")
    private Boolean enviada = false;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Builder.Default
    @Column(name = "intentos_envio")
    private Integer intentosEnvio = 0;

    @Column(name = "error_envio", columnDefinition = "TEXT")
    private String errorEnvio;

    @Column(name = "url_accion", length = 500)
    private String urlAccion;

    @Builder.Default
    @Column(name = "prioridad", length = 20)
    private String prioridad = "normal"; // baja, normal, alta, urgente

    @Column(name = "expira_en")
    private LocalDateTime expiraEn;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Métodos de utilidad
    public boolean estaLeida() {
        return leida != null && leida;
    }

    public boolean estaEnviada() {
        return enviada != null && enviada;
    }

    public boolean estaExpirada() {
        return expiraEn != null && expiraEn.isBefore(LocalDateTime.now());
    }

    public void marcarComoLeida() {
        this.leida = true;
        this.fechaLectura = LocalDateTime.now();
    }

    public void marcarComoEnviada() {
        this.enviada = true;
        this.fechaEnvio = LocalDateTime.now();
    }

    public void registrarErrorEnvio(String error) {
        this.intentosEnvio++;
        this.errorEnvio = error;
    }

    public boolean puedeReintentar() {
        return intentosEnvio < 3 && !estaExpirada();
    }

    public boolean esAltaPrioridad() {
        return "alta".equals(prioridad) || "urgente".equals(prioridad);
    }
}