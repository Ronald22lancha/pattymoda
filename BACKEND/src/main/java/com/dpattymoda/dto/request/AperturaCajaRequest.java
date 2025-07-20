package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para apertura de caja
 */
@Data
@Schema(description = "Datos para apertura de turno de caja")
public class AperturaCajaRequest {

    @Schema(description = "Monto inicial en efectivo", example = "200.00")
    @NotNull(message = "El monto inicial es requerido")
    @PositiveOrZero(message = "El monto inicial debe ser positivo o cero")
    private BigDecimal montoInicial;

    @Schema(description = "Observaciones de apertura")
    private String observaciones;

    @Schema(description = "Detalle del arqueo inicial en JSON")
    private String arqueoDetalle;
}