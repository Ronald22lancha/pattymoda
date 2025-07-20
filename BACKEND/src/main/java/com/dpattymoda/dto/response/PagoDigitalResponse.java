package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de pago digital
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de pago digital")
public class PagoDigitalResponse {

    @Schema(description = "Referencia externa del pago")
    private String referenciaExterna;

    @Schema(description = "Estado del pago", example = "pendiente")
    private String estado;

    @Schema(description = "Método de pago utilizado", example = "yape")
    private String metodoPago;

    @Schema(description = "Monto del pago", example = "150.50")
    private BigDecimal monto;

    @Schema(description = "URL del QR para pago")
    private String urlQR;

    @Schema(description = "Código QR en base64")
    private String codigoQR;

    @Schema(description = "URL de redirección tras pago")
    private String urlRedireccion;

    @Schema(description = "Tiempo de expiración del pago")
    private LocalDateTime fechaExpiracion;

    @Schema(description = "Mensaje para el usuario")
    private String mensaje;

    @Schema(description = "Datos adicionales de la respuesta")
    private String datosRespuesta;

    @Schema(description = "Éxito de la operación", example = "true")
    private Boolean exitoso;
}