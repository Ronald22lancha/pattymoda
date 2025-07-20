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
 * DTO para respuesta de producto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del producto")
public class ProductoResponse {

    @Schema(description = "ID del producto")
    private UUID id;

    @Schema(description = "Código del producto", example = "CAM-001")
    private String codigoProducto;

    @Schema(description = "Nombre del producto", example = "Camisa Casual Manga Larga")
    private String nombreProducto;

    @Schema(description = "Descripción del producto")
    private String descripcion;

    @Schema(description = "Descripción corta")
    private String descripcionCorta;

    @Schema(description = "Información de la categoría")
    private CategoriaBasicaResponse categoria;

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

    @Schema(description = "Costo del producto", example = "45.00")
    private BigDecimal costoProducto;

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

    @Schema(description = "Stock total disponible", example = "45")
    private Integer stockTotal;

    @Schema(description = "Stock disponible para venta", example = "42")
    private Integer stockDisponible;

    @Schema(description = "Tiene stock disponible", example = "true")
    private Boolean tieneStock;

    @Schema(description = "Variantes disponibles")
    private List<VarianteProductoResponse> variantes;

    @Schema(description = "Tallas disponibles")
    private List<String> tallasDisponibles;

    @Schema(description = "Colores disponibles")
    private List<String> coloresDisponibles;

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
    @Schema(description = "Información básica de categoría")
    public static class CategoriaBasicaResponse {
        
        @Schema(description = "ID de la categoría")
        private UUID id;

        @Schema(description = "Nombre de la categoría")
        private String nombreCategoria;

        @Schema(description = "Ruta completa de la categoría")
        private String rutaCompleta;
    }
}