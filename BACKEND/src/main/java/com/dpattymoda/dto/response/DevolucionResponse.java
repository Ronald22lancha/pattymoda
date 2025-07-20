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
 * DTO para respuesta de devolución
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de la devolución")
public class DevolucionResponse {

    @Schema(description = "ID de la devolución")
    private UUID id;

    @Schema(description = "Número de devolución")
    private String numeroDevolucion;

    @Schema(description = "Información del pedido")
    private PedidoBasicoResponse pedido;

    @Schema(description = "Información del cliente")
    private UsuarioBasicoResponse cliente;

    @Schema(description = "Tipo de devolución", example = "devolucion")
    private String tipo;

    @Schema(description = "Motivo de la devolución")
    private String motivo;

    @Schema(description = "Descripción detallada")
    private String descripcionDetallada;

    @Schema(description = "Estado de la devolución", example = "solicitada")
    private String estado;

    @Schema(description = "Items de la devolución")
    private List<ItemDevolucionResponse> itemsDevolucion;

    @Schema(description = "Monto de reembolso", example = "150.00")
    private BigDecimal montoDevolucion;

    @Schema(description = "Método de reembolso", example = "transferencia")
    private String metodoReembolso;

    @Schema(description = "Fecha de solicitud")
    private LocalDateTime fechaSolicitud;

    @Schema(description = "Fecha de aprobación")
    private LocalDateTime fechaAprobacion;

    @Schema(description = "Fecha de recepción de items")
    private LocalDateTime fechaRecepcionItems;

    @Schema(description = "Fecha de completada")
    private LocalDateTime fechaCompletada;

    @Schema(description = "Evidencia fotográfica")
    private List<String> evidenciaFotos;

    @Schema(description = "Empleado que aprobó")
    private UsuarioBasicoResponse aprobadoPor;

    @Schema(description = "Empleado que procesó")
    private UsuarioBasicoResponse procesadoPor;

    @Schema(description = "Notas del cliente")
    private String notasCliente;

    @Schema(description = "Notas internas")
    private String notasInternas;

    @Schema(description = "Costo de envío de devolución")
    private BigDecimal costoEnvioDevolucion;

    @Schema(description = "Número de guía de devolución")
    private String numeroGuiaDevolucion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Pedido básico")
    public static class PedidoBasicoResponse {
        private UUID id;
        private String numeroPedido;
        private BigDecimal total;
        private LocalDateTime fechaCreacion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Usuario básico")
    public static class UsuarioBasicoResponse {
        private UUID id;
        private String nombreCompleto;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Item de devolución")
    public static class ItemDevolucionResponse {
        private UUID id;
        private String nombreProducto;
        private String sku;
        private Integer cantidadSolicitada;
        private Integer cantidadAprobada;
        private BigDecimal precioUnitario;
        private BigDecimal montoReembolso;
        private String estadoItem;
        private String observaciones;
    }
}