package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para solicitud de pago digital
 */
@Data
@Schema(description = "Datos para pago con billeteras digitales")
public class PagoDigitalRequest {

    @Schema(description = "Número de teléfono del pagador", example = "987654321")
    @NotBlank(message = "El número de teléfono es requerido")
    private String numeroTelefono;

    @Schema(description = "Monto a pagar", example = "150.50")
    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;

    @Schema(description = "Concepto del pago", example = "Compra en DPattyModa")
    private String concepto;

    @Schema(description = "Email del cliente para notificaciones")
    private String emailCliente;

    @Schema(description = "Datos adicionales del pago")
    private String datosAdicionales;
}