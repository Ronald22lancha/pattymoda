package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para actualizar devolución
 */
@Data
@Schema(description = "Datos para actualizar devolución")
public class DevolucionUpdateRequest {

    @Schema(description = "Estado de la devolución", example = "procesando")
    private String estado;

    @Schema(description = "Monto de reembolso calculado")
    private BigDecimal montoDevolucion;

    @Schema(description = "Método de reembolso", example = "transferencia")
    private String metodoReembolso;

    @Schema(description = "Número de guía de devolución")
    private String numeroGuiaDevolucion;

    @Schema(description = "Costo de envío de devolución")
    private BigDecimal costoEnvioDevolucion;

    @Schema(description = "Observaciones del empleado")
    private String notasInternas;

    @Schema(description = "URLs de fotos adicionales")
    private List<String> evidenciaFotosAdicionales;

    @Schema(description = "Items aprobados para devolución")
    private List<ItemAprobadoRequest> itemsAprobados;

    @Data
    @Schema(description = "Item aprobado para devolución")
    public static class ItemAprobadoRequest {
        private String detallePedidoId;
        private Integer cantidadAprobada;
        private BigDecimal montoReembolso;
        private String estadoItem;
        private String observaciones;
    }
}