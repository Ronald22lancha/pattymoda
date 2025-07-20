package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para respuesta de mensaje
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del mensaje")
public class MensajeResponse {

    @Schema(description = "ID del mensaje")
    private UUID id;

    @Schema(description = "ID del chat")
    private UUID chatId;

    @Schema(description = "Información del remitente")
    private RemitenteResponse remitente;

    @Schema(description = "Tipo de remitente", example = "cliente")
    private String tipoRemitente;

    @Schema(description = "Contenido del mensaje")
    private String contenido;

    @Schema(description = "Tipo de mensaje", example = "texto")
    private String tipoMensaje;

    @Schema(description = "Archivos adjuntos")
    private List<String> archivosAdjuntos;

    @Schema(description = "ID del mensaje padre")
    private UUID mensajePadreId;

    @Schema(description = "Estado del mensaje", example = "enviado")
    private String estado;

    @Schema(description = "Editado", example = "false")
    private Boolean editado;

    @Schema(description = "Fecha de edición")
    private LocalDateTime fechaEdicion;

    @Schema(description = "Moderado", example = "false")
    private Boolean moderado;

    @Schema(description = "Fecha de lectura")
    private LocalDateTime fechaLectura;

    @Schema(description = "Reacciones al mensaje")
    private String reacciones;

    @Schema(description = "Fecha de envío")
    private LocalDateTime fechaEnvio;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información del remitente")
    public static class RemitenteResponse {
        private UUID id;
        private String nombreCompleto;
        private String email;
        private String rol;
    }
}