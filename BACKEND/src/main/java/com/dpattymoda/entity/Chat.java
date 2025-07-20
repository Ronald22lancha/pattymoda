package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad Chat para conversaciones en tiempo real
 */
@Entity
@Table(name = "chats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_asignado_id")
    private Usuario empleadoAsignado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "asunto", length = 200)
    private String asunto;

    @Builder.Default
    @Column(name = "estado", length = 20)
    private String estado = "abierto"; // abierto, en_progreso, cerrado, escalado

    @Builder.Default
    @Column(name = "prioridad", length = 20)
    private String prioridad = "normal"; // baja, normal, alta, urgente

    @Column(name = "categoria", length = 50)
    private String categoria; // consulta_producto, problema_pedido, devolucion, general

    @Column(name = "etiquetas")
    private String[] etiquetas;

    @Column(name = "satisfaccion_cliente")
    private Integer satisfaccionCliente; // 1-5 estrellas

    @Column(name = "comentario_satisfaccion", columnDefinition = "TEXT")
    private String comentarioSatisfaccion;

    @Builder.Default
    @Column(name = "fecha_primer_mensaje")
    private LocalDateTime fechaPrimerMensaje = LocalDateTime.now();

    @Builder.Default
    @Column(name = "fecha_ultimo_mensaje")
    private LocalDateTime fechaUltimoMensaje = LocalDateTime.now();

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "tiempo_primera_respuesta")
    private Duration tiempoPrimeraRespuesta;

    @Column(name = "tiempo_resolucion")
    private Duration tiempoResolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cerrado_por")
    private Usuario cerradoPor;

    @Column(name = "motivo_cierre", length = 100)
    private String motivoCierre;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relaciones
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Mensaje> mensajes = new ArrayList<>();

    // Métodos de utilidad
    public boolean estaAbierto() {
        return "abierto".equals(estado);
    }

    public boolean estaCerrado() {
        return "cerrado".equals(estado);
    }

    public boolean estaEscalado() {
        return "escalado".equals(estado);
    }

    public boolean tieneEmpleadoAsignado() {
        return empleadoAsignado != null;
    }

    public void asignarEmpleado(Usuario empleado) {
        this.empleadoAsignado = empleado;
        this.estado = "en_progreso";
    }

    public void cerrarChat(Usuario usuario, String motivo) {
        this.estado = "cerrado";
        this.fechaCierre = LocalDateTime.now();
        this.cerradoPor = usuario;
        this.motivoCierre = motivo;
        
        if (fechaPrimerMensaje != null) {
            this.tiempoResolucion = Duration.between(fechaPrimerMensaje, fechaCierre);
        }
    }

    public void escalar(String motivo) {
        this.estado = "escalado";
        this.prioridad = "alta";
        this.motivoCierre = "Escalado: " + motivo;
    }

    public void calificar(Integer satisfaccion, String comentario) {
        this.satisfaccionCliente = satisfaccion;
        this.comentarioSatisfaccion = comentario;
    }

    public int getTotalMensajes() {
        return mensajes != null ? mensajes.size() : 0;
    }

    public long getMensajesNoLeidos(UUID usuarioId) {
        if (mensajes == null) return 0;
        
        return mensajes.stream()
                      .filter(m -> !m.getRemitente().getId().equals(usuarioId))
                      .filter(m -> m.getFechaLectura() == null)
                      .count();
    }

    public boolean esAltaPrioridad() {
        return "alta".equals(prioridad) || "urgente".equals(prioridad);
    }
}