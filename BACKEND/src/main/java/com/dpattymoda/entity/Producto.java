package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad Producto para el catálogo de DPattyModa
 */
@Entity
@Table(name = "productos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "codigo_producto", nullable = false, unique = true, length = 50)
    private String codigoProducto;

    @Column(name = "nombre_producto", nullable = false, length = 200)
    private String nombreProducto;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "descripcion_corta", length = 500)
    private String descripcionCorta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(name = "marca", length = 100)
    private String marca;

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "precio_oferta", precision = 10, scale = 2)
    private BigDecimal precioOferta;

    @Column(name = "costo_producto", precision = 10, scale = 2)
    private BigDecimal costoProducto;

    @Column(name = "margen_ganancia", precision = 5, scale = 2)
    private BigDecimal margenGanancia;

    @Column(name = "peso", precision = 8, scale = 3)
    private BigDecimal peso;

    @Column(name = "dimensiones", columnDefinition = "jsonb")
    private String dimensiones;

    @Column(name = "caracteristicas", columnDefinition = "jsonb")
    private String caracteristicas;

    @Column(name = "imagenes", columnDefinition = "jsonb")
    private String imagenes;

    @Column(name = "tags")
    private String[] tags;

    @Builder.Default
    @Column(name = "activo")
    private Boolean activo = true;

    @Builder.Default
    @Column(name = "destacado")
    private Boolean destacado = false;

    @Builder.Default
    @Column(name = "nuevo")
    private Boolean nuevo = false;

    @Column(name = "fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    @Builder.Default
    @Column(name = "calificacion_promedio", precision = 3, scale = 2)
    private BigDecimal calificacionPromedio = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_reseñas")
    private Integer totalReseñas = 0;

    @Builder.Default
    @Column(name = "total_ventas")
    private Integer totalVentas = 0;

    @Column(name = "seo_titulo", length = 200)
    private String seoTitulo;

    @Column(name = "seo_descripcion", columnDefinition = "TEXT")
    private String seoDescripcion;

    @Column(name = "seo_palabras_clave")
    private String[] seoPalabrasClave;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relaciones
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VarianteProducto> variantes = new ArrayList<>();

    // Métodos de utilidad
    public BigDecimal getPrecioVenta() {
        return precioOferta != null && precioOferta.compareTo(BigDecimal.ZERO) > 0 
            ? precioOferta : precioBase;
    }

    public boolean tieneOferta() {
        return precioOferta != null && precioOferta.compareTo(precioBase) < 0;
    }

    public BigDecimal getPorcentajeDescuento() {
        if (!tieneOferta()) {
            return BigDecimal.ZERO;
        }
        BigDecimal descuento = precioBase.subtract(precioOferta);
        return descuento.divide(precioBase, 4, BigDecimal.ROUND_HALF_UP)
                       .multiply(BigDecimal.valueOf(100));
    }

    public boolean estaActivo() {
        return activo != null && activo;
    }

    public boolean esNuevo() {
        return nuevo != null && nuevo;
    }

    public boolean esDestacado() {
        return destacado != null && destacado;
    }
}