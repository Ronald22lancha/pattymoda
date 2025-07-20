package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para actualizar estado de envío
 */
@Data
@Schema(description = "Datos para actualizar envío")
public class ActualizarEnvioRequest {

    @Schema(description = "Nuevo estado", example = "en_camino")
    @NotBlank(message = "El estado es requerido")
    private String estado; // preparando, en_camino, entregado, devuelto

    @Schema(description = "Ubicación actual")
    private String ubicacionActual;

    @Schema(description = "Observaciones del estado")
    private String observaciones;

    @Schema(description = "Fecha estimada de entrega")
    private LocalDateTime fechaEntregaEstimada;

    @Schema(description = "Evidencia de entrega (URL)")
    private String evidenciaEntrega;

    @Schema(description = "Nombre de quien recibió")
    private String nombreReceptor;

    @Schema(description = "DNI de quien recibió")
    private String dniReceptor;

    @Schema(description = "Relación con destinatario")
    private String relacionDestinatario;

    @Schema(description = "Motivo si no se pudo entregar")
    private String motivoNoEntrega;
}