package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para respuesta de envío
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del envío")
public class EnvioResponse {

    @Schema(description = "ID del envío")
    private UUID id;

    @Schema(description = "Información del pedido")
    private PedidoEnvioResponse pedido;

    @Schema(description = "Transportista")
    private String transportista;

    @Schema(description = "Número de guía")
    private String numeroGuia;

    @Schema(description = "Estado del envío", example = "en_camino")
    private String estado;

    @Schema(description = "Fecha de despacho")
    private LocalDateTime fechaDespacho;

    @Schema(description = "Fecha estimada de entrega")
    private LocalDateTime fechaEntregaEstimada;

    @Schema(description = "Fecha real de entrega")
    private LocalDateTime fechaEntregaReal;

    @Schema(description = "Costo del envío", example = "15.00")
    private BigDecimal costoEnvio;

    @Schema(description = "Peso total", example = "1.250")
    private BigDecimal pesoTotal;

    @Schema(description = "Dimensiones del paquete")
    private String dimensiones;

    @Schema(description = "Dirección de origen")
    private String direccionOrigen;

    @Schema(description = "Dirección de destino")
    private String direccionDestino;

    @Schema(description = "Instrucciones de entrega")
    private String instruccionesEntrega;

    @Schema(description = "Evidencia de entrega")
    private List<String> evidenciaEntrega;

    @Schema(description = "Seguimiento del envío")
    private List<EventoSeguimientoResponse> seguimiento;

    @Schema(description = "Intentos de entrega", example = "1")
    private Integer intentosEntrega;

    @Schema(description = "Máximo intentos", example = "3")
    private Integer maxIntentosEntrega;

    @Schema(description = "Fecha de creación")
    private LocalDateTime fechaCreacion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información del pedido para envío")
    public static class PedidoEnvioResponse {
        private UUID id;
        private String numeroPedido;
        private String nombreCliente;
        private String telefonoCliente;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Evento de seguimiento")
    public static class EventoSeguimientoResponse {
        private LocalDateTime fecha;
        private String estado;
        private String descripcion;
        private String ubicacion;
        private String observaciones;
    }
}