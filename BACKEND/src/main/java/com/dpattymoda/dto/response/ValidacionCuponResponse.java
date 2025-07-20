package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para respuesta de validación de cupón
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resultado de validación de cupón")
public class ValidacionCuponResponse {

    @Schema(description = "Cupón válido", example = "true")
    private Boolean valido;

    @Schema(description = "ID del cupón")
    private UUID cuponId;

    @Schema(description = "Código del cupón")
    private String codigoCupon;

    @Schema(description = "Nombre del cupón")
    private String nombreCupon;

    @Schema(description = "Tipo de descuento", example = "porcentaje")
    private String tipoDescuento;

    @Schema(description = "Valor del descuento", example = "15.00")
    private BigDecimal valorDescuento;

    @Schema(description = "Monto del descuento calculado", example = "22.50")
    private BigDecimal montoDescuento;

    @Schema(description = "Mensaje de validación")
    private String mensaje;

    @Schema(description = "Código de error si no es válido")
    private String codigoError;

    @Schema(description = "Detalles adicionales")
    private String detalles;
}