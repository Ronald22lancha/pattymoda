package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para crear notificación
 */
@Data
@Schema(description = "Datos para crear notificación")
public class NotificacionCreateRequest {

    @Schema(description = "Tipo de notificación", example = "pedido_confirmado")
    @NotBlank(message = "El tipo de notificación es requerido")
    private String tipo;

    @Schema(description = "Título de la notificación", example = "Pedido Confirmado")
    @NotBlank(message = "El título es requerido")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;

    @Schema(description = "Mensaje de la notificación")
    @NotBlank(message = "El mensaje es requerido")
    private String mensaje;

    @Schema(description = "Canal de envío", example = "email")
    @NotBlank(message = "El canal es requerido")
    private String canal; // email, push, sms, in_app

    @Schema(description = "Datos adicionales")
    private Map<String, Object> datos;

    @Schema(description = "URL de acción")
    private String urlAccion;

    @Schema(description = "Prioridad", example = "normal")
    private String prioridad = "normal"; // baja, normal, alta, urgente

    @Schema(description = "Fecha de expiración")
    private LocalDateTime expiraEn;

    @Schema(description = "Programar envío")
    private LocalDateTime programarPara;
}