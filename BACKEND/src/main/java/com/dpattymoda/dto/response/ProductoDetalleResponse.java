package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para respuesta detallada de producto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información detallada del producto")
public class ProductoDetalleResponse {

    @Schema(description = "ID del producto")
    private UUID id;

    @Schema(description = "Código del producto", example = "CAM-001")
    private String codigoProducto;

    @Schema(description = "Nombre del producto", example = "Camisa Casual Manga Larga")
    private String nombreProducto;

    @Schema(description = "Descripción completa del producto")
    private String descripcion;

    @Schema(description = "Descripción corta")
    private String descripcionCorta;

    @Schema(description = "Información de la categoría")
    private CategoriaDetalleResponse categoria;

    @Schema(description = "Marca", example = "DPattyModa")
    private String marca;

    @Schema(description = "Precio base", example = "75.00")
    private BigDecimal precioBase;

    @Schema(description = "Precio de oferta", example = "67.50")
    private BigDecimal precioOferta;

    @Schema(description = "Precio de venta final", example = "67.50")
    private BigDecimal precioVenta;

    @Schema(description = "Porcentaje de descuento", example = "10.00")
    private BigDecimal porcentajeDescuento;

    @Schema(description = "Tiene oferta activa", example = "true")
    private Boolean tieneOferta;

    @Schema(description = "Peso en kilogramos", example = "0.250")
    private BigDecimal peso;

    @Schema(description = "Dimensiones del producto")
    private String dimensiones;

    @Schema(description = "Características del producto")
    private String caracteristicas;

    @Schema(description = "Imágenes del producto")
    private List<String> imagenes;

    @Schema(description = "Tags del producto")
    private List<String> tags;

    @Schema(description = "Producto activo", example = "true")
    private Boolean activo;

    @Schema(description = "Producto destacado", example = "false")
    private Boolean destacado;

    @Schema(description = "Producto nuevo", example = "true")
    private Boolean nuevo;

    @Schema(description = "Fecha de lanzamiento")
    private LocalDate fechaLanzamiento;

    @Schema(description = "Calificación promedio", example = "4.5")
    private BigDecimal calificacionPromedio;

    @Schema(description = "Total de reseñas", example = "23")
    private Integer totalReseñas;

    @Schema(description = "Total de ventas", example = "156")
    private Integer totalVentas;

    @Schema(description = "Variantes del producto")
    private List<VarianteProductoResponse> variantes;

    @Schema(description = "Tallas disponibles")
    private List<String> tallasDisponibles;

    @Schema(description = "Colores disponibles")
    private List<String> coloresDisponibles;

    @Schema(description = "Reseñas del producto")
    private List<ResenaBasicaResponse> reseñas;

    @Schema(description = "Productos relacionados")
    private List<ProductoBasicoResponse> productosRelacionados;

    @Schema(description = "Título SEO")
    private String seoTitulo;

    @Schema(description = "Descripción SEO")
    private String seoDescripcion;

    @Schema(description = "Palabras clave SEO")
    private List<String> seoPalabrasClave;

    @Schema(description = "Fecha de creación")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de actualización")
    private LocalDateTime fechaActualizacion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información detallada de categoría")
    public static class CategoriaDetalleResponse {
        
        @Schema(description = "ID de la categoría")
        private UUID id;

        @Schema(description = "Nombre de la categoría")
        private String nombreCategoria;

        @Schema(description = "Descripción de la categoría")
        private String descripcion;

        @Schema(description = "Ruta completa de la categoría")
        private String rutaCompleta;

        @Schema(description = "Imagen de la categoría")
        private String imagenUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información básica de reseña")
    public static class ResenaBasicaResponse {
        
        @Schema(description = "ID de la reseña")
        private UUID id;

        @Schema(description = "Calificación", example = "5")
        private Integer calificacion;

        @Schema(description = "Título de la reseña")
        private String titulo;

        @Schema(description = "Comentario")
        private String comentario;

        @Schema(description = "Nombre del usuario")
        private String nombreUsuario;

        @Schema(description = "Fecha de la reseña")
        private LocalDateTime fechaResena;

        @Schema(description = "Verificada (compra real)", example = "true")
        private Boolean verificada;

        @Schema(description = "Utilidad positiva", example = "12")
        private Integer utilidadPositiva;
    }
}