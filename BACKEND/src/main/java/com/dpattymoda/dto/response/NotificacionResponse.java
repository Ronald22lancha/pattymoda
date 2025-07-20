package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO para respuesta de notificación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de la notificación")
public class NotificacionResponse {

    @Schema(description = "ID de la notificación")
    private UUID id;

    @Schema(description = "Tipo de notificación", example = "pedido_confirmado")
    private String tipo;

    @Schema(description = "Título de la notificación")
    private String titulo;

    @Schema(description = "Mensaje de la notificación")
    private String mensaje;

    @Schema(description = "Canal de envío", example = "email")
    private String canal;

    @Schema(description = "Datos adicionales")
    private Map<String, Object> datos;

    @Schema(description = "URL de acción")
    private String urlAccion;

    @Schema(description = "Prioridad", example = "normal")
    private String prioridad;

    @Schema(description = "Leída", example = "false")
    private Boolean leida;

    @Schema(description = "Fecha de lectura")
    private LocalDateTime fechaLectura;

    @Schema(description = "Enviada", example = "true")
    private Boolean enviada;

    @Schema(description = "Fecha de envío")
    private LocalDateTime fechaEnvio;

    @Schema(description = "Intentos de envío", example = "1")
    private Integer intentosEnvio;

    @Schema(description = "Error de envío")
    private String errorEnvio;

    @Schema(description = "Fecha de expiración")
    private LocalDateTime expiraEn;

    @Schema(description = "Fecha de creación")
    private LocalDateTime fechaCreacion;
}