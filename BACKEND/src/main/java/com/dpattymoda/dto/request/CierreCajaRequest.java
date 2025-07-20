package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para cierre de caja
 */
@Data
@Schema(description = "Datos para cierre de turno de caja")
public class CierreCajaRequest {

    @Schema(description = "Monto final contado en efectivo", example = "450.00")
    @NotNull(message = "El monto final es requerido")
    @PositiveOrZero(message = "El monto final debe ser positivo o cero")
    private BigDecimal montoFinal;

    @Schema(description = "Observaciones de cierre")
    private String observaciones;

    @Schema(description = "Detalle del arqueo final en JSON")
    private String arqueoDetalle;
}