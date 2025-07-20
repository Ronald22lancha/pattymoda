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
 * Entidad Categoria para organizar productos
 */
@Entity
@Table(name = "categorias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nombre_categoria", nullable = false, length = 100)
    private String nombreCategoria;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_padre_id")
    private Categoria categoriaPadre;

    @Builder.Default
    @Column(name = "nivel")
    private Integer nivel = 1;

    @Builder.Default
    @Column(name = "orden_visualizacion")
    private Integer ordenVisualizacion = 0;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Builder.Default
    @Column(name = "activa")
    private Boolean activa = true;

    @Column(name = "seo_titulo", length = 200)
    private String seoTitulo;

    @Column(name = "seo_descripcion", columnDefinition = "TEXT")
    private String seoDescripcion;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relaciones
    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Categoria> subcategorias = new ArrayList<>();

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();

    // Métodos de utilidad
    public boolean estaActiva() {
        return activa != null && activa;
    }

    public boolean esCategoriaPrincipal() {
        return categoriaPadre == null;
    }

    public boolean tieneSubcategorias() {
        return subcategorias != null && !subcategorias.isEmpty();
    }

    public String getRutaCompleta() {
        if (categoriaPadre == null) {
            return nombreCategoria;
        }
        return categoriaPadre.getRutaCompleta() + " > " + nombreCategoria;
    }
}