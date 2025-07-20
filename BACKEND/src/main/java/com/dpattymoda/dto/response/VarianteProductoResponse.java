package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para respuesta de variante de producto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de variante de producto")
public class VarianteProductoResponse {

    @Schema(description = "ID de la variante")
    private UUID id;

    @Schema(description = "SKU de la variante", example = "CAM-001-M-AZUL")
    private String sku;

    @Schema(description = "Talla", example = "M")
    private String talla;

    @Schema(description = "Color", example = "Azul")
    private String color;

    @Schema(description = "Material", example = "Algodón 100%")
    private String material;

    @Schema(description = "Precio de la variante", example = "75.00")
    private BigDecimal precioVariante;

    @Schema(description = "Precio final (considerando ofertas)", example = "67.50")
    private BigDecimal precioFinal;

    @Schema(description = "Imagen específica de la variante")
    private String imagenVariante;

    @Schema(description = "Código de barras")
    private String codigoBarras;

    @Schema(description = "Stock total disponible", example = "15")
    private Integer stockTotal;

    @Schema(description = "Stock disponible para venta", example = "12")
    private Integer stockDisponible;

    @Schema(description = "Información básica del producto")
    private ProductoBasicoResponse producto;

    @Schema(description = "Estado activo", example = "true")
    private Boolean activo;
}