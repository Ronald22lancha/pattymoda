package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO para crear mensaje en chat
 */
@Data
@Schema(description = "Datos para enviar mensaje")
public class MensajeCreateRequest {

    @Schema(description = "Contenido del mensaje")
    @NotBlank(message = "El contenido del mensaje es requerido")
    private String contenido;

    @Schema(description = "Tipo de mensaje", example = "texto")
    private String tipoMensaje = "texto";

    @Schema(description = "URLs de archivos adjuntos")
    private List<String> archivosAdjuntos;

    @Schema(description = "ID del mensaje padre (para respuestas)")
    private UUID mensajePadreId;
}