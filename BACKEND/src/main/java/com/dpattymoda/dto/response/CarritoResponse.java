package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para respuesta del carrito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del carrito de compras")
public class CarritoResponse {

    @Schema(description = "ID del carrito")
    private UUID id;

    @Schema(description = "ID del usuario (null para invitados)")
    private UUID usuarioId;

    @Schema(description = "ID de sesión para invitados")
    private String sesionId;

    @Schema(description = "Estado del carrito", example = "activo")
    private String estado;

    @Schema(description = "Subtotal del carrito", example = "150.00")
    private BigDecimal subtotal;

    @Schema(description = "Descuento aplicado", example = "15.00")
    private BigDecimal descuento;

    @Schema(description = "Impuestos calculados", example = "24.30")
    private BigDecimal impuestos;

    @Schema(description = "Costo de envío", example = "10.00")
    private BigDecimal costoEnvio;

    @Schema(description = "Total del carrito", example = "169.30")
    private BigDecimal total;

    @Schema(description = "Cantidad total de items", example = "5")
    private Integer cantidadTotalItems;

    @Schema(description = "Items del carrito")
    private List<DetalleCarritoResponse> detalles;

    @Schema(description = "Cupones aplicados")
    private List<String> cuponesAplicados;

    @Schema(description = "Fecha de creación")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime fechaActualizacion;

    @Schema(description = "Fecha de expiración")
    private LocalDateTime fechaExpiracion;
}