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
 * Entidad Resena para calificaciones de productos
 */
@Entity
@Table(name = "reseñas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido; // Para verificar compra real

    @Column(name = "calificacion", nullable = false)
    private Integer calificacion; // 1-5 estrellas

    @Column(name = "titulo", length = 200)
    private String titulo;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "ventajas")
    private String[] ventajas;

    @Column(name = "desventajas")
    private String[] desventajas;

    @Column(name = "recomendaria")
    private Boolean recomendaria;

    @Builder.Default
    @Column(name = "verificada")
    private Boolean verificada = false; // Si el usuario realmente compró el producto

    @Builder.Default
    @Column(name = "estado_moderacion", length = 20)
    private String estadoModeracion = "pendiente"; // pendiente, aprobada, rechazada

    @Column(name = "fecha_moderacion")
    private LocalDateTime fechaModeracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderado_por")
    private Usuario moderadoPor;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Builder.Default
    @Column(name = "utilidad_positiva")
    private Integer utilidadPositiva = 0; // "Me fue útil" positivos

    @Builder.Default
    @Column(name = "utilidad_negativa")
    private Integer utilidadNegativa = 0; // "No me fue útil"

    @Builder.Default
    @Column(name = "reportes")
    private Integer reportes = 0;

    @Column(name = "imagenes", columnDefinition = "jsonb")
    private String imagenes; // URLs de imágenes subidas

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_comprada_id")
    private VarianteProducto varianteComprada;

    @Column(name = "talla_comprada", length = 20)
    private String tallaComprada;

    @Column(name = "color_comprado", length = 50)
    private String colorComprado;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    @Builder.Default
    @Column(name = "fecha_resena")
    private LocalDateTime fechaResena = LocalDateTime.now();

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Métodos de utilidad
    public boolean estaAprobada() {
        return "aprobada".equals(estadoModeracion);
    }

    public boolean estaRechazada() {
        return "rechazada".equals(estadoModeracion);
    }

    public boolean estaPendiente() {
        return "pendiente".equals(estadoModeracion);
    }

    public boolean estaVerificada() {
        return verificada != null && verificada;
    }

    public void aprobar(Usuario moderador) {
        this.estadoModeracion = "aprobada";
        this.moderadoPor = moderador;
        this.fechaModeracion = LocalDateTime.now();
        this.motivoRechazo = null;
    }

    public void rechazar(Usuario moderador, String motivo) {
        this.estadoModeracion = "rechazada";
        this.moderadoPor = moderador;
        this.fechaModeracion = LocalDateTime.now();
        this.motivoRechazo = motivo;
    }

    public void marcarComoUtil(boolean util) {
        if (util) {
            this.utilidadPositiva++;
        } else {
            this.utilidadNegativa++;
        }
    }

    public void reportar() {
        this.reportes++;
    }

    public double getPorcentajeUtilidad() {
        int total = utilidadPositiva + utilidadNegativa;
        if (total == 0) return 0;
        return (double) utilidadPositiva / total * 100;
    }

    public boolean esCalificacionAlta() {
        return calificacion != null && calificacion >= 4;
    }

    public boolean esCalificacionBaja() {
        return calificacion != null && calificacion <= 2;
    }

    public boolean tieneImagenes() {
        return imagenes != null && !imagenes.isEmpty() && !"[]".equals(imagenes);
    }

    public boolean tieneComentario() {
        return comentario != null && !comentario.trim().isEmpty();
    }

    public String getResumenCalificacion() {
        if (calificacion == null) return "Sin calificar";
        
        switch (calificacion) {
            case 1: return "Muy malo";
            case 2: return "Malo";
            case 3: return "Regular";
            case 4: return "Bueno";
            case 5: return "Excelente";
            default: return "Sin calificar";
        }
    }
}