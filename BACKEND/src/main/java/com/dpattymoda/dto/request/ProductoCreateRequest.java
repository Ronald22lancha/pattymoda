package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO para creación de producto
 */
@Data
@Schema(description = "Datos para crear nuevo producto")
public class ProductoCreateRequest {

    @Schema(description = "Código único del producto", example = "CAM-001")
    @NotBlank(message = "El código del producto es requerido")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String codigoProducto;

    @Schema(description = "Nombre del producto", example = "Camisa Casual Manga Larga")
    @NotBlank(message = "El nombre del producto es requerido")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombreProducto;

    @Schema(description = "Descripción completa del producto")
    private String descripcion;

    @Schema(description = "Descripción corta para listados")
    @Size(max = 500, message = "La descripción corta no puede exceder 500 caracteres")
    private String descripcionCorta;

    @Schema(description = "ID de la categoría")
    @NotNull(message = "La categoría es requerida")
    private UUID categoriaId;

    @Schema(description = "Marca del producto", example = "DPattyModa")
    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String marca;

    @Schema(description = "Precio base del producto", example = "75.00")
    @NotNull(message = "El precio base es requerido")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precioBase;

    @Schema(description = "Precio de oferta", example = "67.50")
    private BigDecimal precioOferta;

    @Schema(description = "Costo del producto", example = "45.00")
    private BigDecimal costoProducto;

    @Schema(description = "Peso en kilogramos", example = "0.250")
    private BigDecimal peso;

    @Schema(description = "Dimensiones del producto en JSON")
    private String dimensiones;

    @Schema(description = "Características del producto en JSON")
    private String caracteristicas;

    @Schema(description = "URLs de imágenes del producto")
    private List<String> imagenes;

    @Schema(description = "Tags para búsqueda")
    private List<String> tags;

    @Schema(description = "Producto destacado", example = "false")
    private Boolean destacado = false;

    @Schema(description = "Producto nuevo", example = "true")
    private Boolean nuevo = false;

    @Schema(description = "Fecha de lanzamiento")
    private LocalDate fechaLanzamiento;

    @Schema(description = "Título SEO")
    @Size(max = 200, message = "El título SEO no puede exceder 200 caracteres")
    private String seoTitulo;

    @Schema(description = "Descripción SEO")
    private String seoDescripcion;

    @Schema(description = "Palabras clave SEO")
    private List<String> seoPalabrasClave;

    @Schema(description = "Variantes del producto (tallas, colores)")
    private List<VarianteCreateRequest> variantes;

    @Data
    @Schema(description = "Datos para crear variante de producto")
    public static class VarianteCreateRequest {
        
        @Schema(description = "SKU único de la variante", example = "CAM-001-M-AZUL")
        @NotBlank(message = "El SKU es requerido")
        private String sku;

        @Schema(description = "Talla", example = "M")
        private String talla;

        @Schema(description = "Color", example = "Azul")
        private String color;

        @Schema(description = "Material", example = "Algodón 100%")
        private String material;

        @Schema(description = "Precio específico de la variante")
        private BigDecimal precioVariante;

        @Schema(description = "Peso específico de la variante")
        private BigDecimal pesoVariante;

        @Schema(description = "Imagen específica de la variante")
        private String imagenVariante;

        @Schema(description = "Código de barras")
        private String codigoBarras;

        @Schema(description = "Stock inicial por sucursal")
        private List<StockInicialRequest> stockInicial;
    }

    @Data
    @Schema(description = "Stock inicial por sucursal")
    public static class StockInicialRequest {
        
        @Schema(description = "ID de la sucursal")
        @NotNull(message = "La sucursal es requerida")
        private UUID sucursalId;

        @Schema(description = "Cantidad inicial", example = "20")
        @NotNull(message = "La cantidad es requerida")
        private Integer cantidad;

        @Schema(description = "Ubicación física", example = "A1-B2")
        private String ubicacionFisica;

        @Schema(description = "Cantidad mínima", example = "5")
        private Integer cantidadMinima = 5;
    }
}