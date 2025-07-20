package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para respuesta del detalle del carrito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalle de item en el carrito")
public class DetalleCarritoResponse {

    @Schema(description = "ID del detalle")
    private UUID id;

    @Schema(description = "Información de la variante del producto")
    private VarianteProductoResponse variante;

    @Schema(description = "Cantidad del producto", example = "2")
    private Integer cantidad;

    @Schema(description = "Precio unitario", example = "75.00")
    private BigDecimal precioUnitario;

    @Schema(description = "Descuento unitario", example = "7.50")
    private BigDecimal descuentoUnitario;

    @Schema(description = "Subtotal del item", example = "135.00")
    private BigDecimal subtotal;

    @Schema(description = "Fecha cuando se agregó al carrito")
    private LocalDateTime fechaAgregado;
}