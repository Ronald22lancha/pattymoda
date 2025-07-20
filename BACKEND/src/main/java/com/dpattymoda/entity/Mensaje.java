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
 * Entidad Mensaje para chat en tiempo real
 */
@Entity
@Table(name = "mensajes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    @Column(name = "tipo_remitente", nullable = false, length = 20)
    private String tipoRemitente; // cliente, empleado, sistema

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Builder.Default
    @Column(name = "tipo_mensaje", length = 20)
    private String tipoMensaje = "texto"; // texto, imagen, archivo, sistema

    @Column(name = "archivos_adjuntos", columnDefinition = "jsonb")
    private String archivosAdjuntos; // URLs de archivos

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensaje_padre_id")
    private Mensaje mensajePadre; // Para respuestas específicas

    @Builder.Default
    @Column(name = "estado", length = 20)
    private String estado = "enviado"; // enviado, entregado, leido

    @Builder.Default
    @Column(name = "editado")
    private Boolean editado = false;

    @Column(name = "fecha_edicion")
    private LocalDateTime fechaEdicion;

    @Builder.Default
    @Column(name = "moderado")
    private Boolean moderado = false;

    @Column(name = "fecha_moderacion")
    private LocalDateTime fechaModeracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderado_por")
    private Usuario moderadoPor;

    @Column(name = "contenido_original", columnDefinition = "TEXT")
    private String contenidoOriginal;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    @Column(name = "reacciones", columnDefinition = "jsonb")
    private String reacciones; // Emojis de reacción

    @Builder.Default
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Métodos de utilidad
    public boolean estaLeido() {
        return fechaLectura != null;
    }

    public boolean estaEditado() {
        return editado != null && editado;
    }

    public boolean estaModerado() {
        return moderado != null && moderado;
    }

    public boolean esDelCliente() {
        return "cliente".equals(tipoRemitente);
    }

    public boolean esDelEmpleado() {
        return "empleado".equals(tipoRemitente);
    }

    public boolean esDelSistema() {
        return "sistema".equals(tipoRemitente);
    }

    public void marcarComoLeido() {
        this.fechaLectura = LocalDateTime.now();
        this.estado = "leido";
    }

    public void editar(String nuevoContenido) {
        if (this.contenidoOriginal == null) {
            this.contenidoOriginal = this.contenido;
        }
        this.contenido = nuevoContenido;
        this.editado = true;
        this.fechaEdicion = LocalDateTime.now();
    }

    public void moderar(Usuario moderador, String contenidoModerado) {
        if (this.contenidoOriginal == null) {
            this.contenidoOriginal = this.contenido;
        }
        this.contenido = contenidoModerado;
        this.moderado = true;
        this.moderadoPor = moderador;
        this.fechaModeracion = LocalDateTime.now();
    }

    public boolean tieneArchivosAdjuntos() {
        return archivosAdjuntos != null && !archivosAdjuntos.isEmpty() && !"[]".equals(archivosAdjuntos);
    }

    public boolean esRespuesta() {
        return mensajePadre != null;
    }
}