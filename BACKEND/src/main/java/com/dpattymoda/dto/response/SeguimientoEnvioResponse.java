package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para seguimiento detallado de envío
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Seguimiento detallado del envío")
public class SeguimientoEnvioResponse {

    @Schema(description = "Número de guía")
    private String numeroGuia;

    @Schema(description = "Estado actual", example = "en_camino")
    private String estadoActual;

    @Schema(description = "Descripción del estado")
    private String descripcionEstado;

    @Schema(description = "Ubicación actual")
    private String ubicacionActual;

    @Schema(description = "Fecha estimada de entrega")
    private LocalDateTime fechaEstimadaEntrega;

    @Schema(description = "Porcentaje de progreso", example = "65")
    private Integer porcentajeProgreso;

    @Schema(description = "Historial de eventos")
    private List<EventoSeguimiento> eventos;

    @Schema(description = "Información del destinatario")
    private DestinatarioInfo destinatario;

    @Schema(description = "Próximo evento esperado")
    private String proximoEvento;

    @Schema(description = "Tiempo estimado restante")
    private String tiempoEstimadoRestante;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Evento de seguimiento")
    public static class EventoSeguimiento {
        private LocalDateTime fecha;
        private String evento;
        private String descripcion;
        private String ubicacion;
        private String responsable;
        private String observaciones;
        private boolean esHito; // Evento importante
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información del destinatario")
    public static class DestinatarioInfo {
        private String nombre;
        private String telefono;
        private String direccion;
        private String ciudad;
        private String departamento;
    }
}