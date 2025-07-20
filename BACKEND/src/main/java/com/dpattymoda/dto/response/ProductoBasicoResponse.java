package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para respuesta básica de producto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información básica del producto")
public class ProductoBasicoResponse {

    @Schema(description = "ID del producto")
    private UUID id;

    @Schema(description = "Código del producto", example = "CAM-001")
    private String codigoProducto;

    @Schema(description = "Nombre del producto", example = "Camisa Casual Manga Larga")
    private String nombreProducto;

    @Schema(description = "Descripción corta")
    private String descripcionCorta;

    @Schema(description = "Marca", example = "DPattyModa")
    private String marca;

    @Schema(description = "Precio base", example = "75.00")
    private BigDecimal precioBase;

    @Schema(description = "Precio de oferta", example = "67.50")
    private BigDecimal precioOferta;

    @Schema(description = "Precio de venta final", example = "67.50")
    private BigDecimal precioVenta;

    @Schema(description = "Imágenes del producto")
    private String[] imagenes;

    @Schema(description = "Calificación promedio", example = "4.5")
    private BigDecimal calificacionPromedio;

    @Schema(description = "Total de reseñas", example = "23")
    private Integer totalReseñas;

    @Schema(description = "Categoría del producto")
    private String nombreCategoria;
}