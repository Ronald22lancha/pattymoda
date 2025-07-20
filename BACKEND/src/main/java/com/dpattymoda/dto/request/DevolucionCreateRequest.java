package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO para crear solicitud de devolución
 */
@Data
@Schema(description = "Datos para solicitar devolución")
public class DevolucionCreateRequest {

    @Schema(description = "ID del pedido")
    @NotNull(message = "El pedido es requerido")
    private UUID pedidoId;

    @Schema(description = "Tipo de devolución", example = "devolucion")
    @NotBlank(message = "El tipo es requerido")
    private String tipo; // devolucion, cambio

    @Schema(description = "Motivo de la devolución", example = "producto_defectuoso")
    @NotBlank(message = "El motivo es requerido")
    private String motivo;

    @Schema(description = "Descripción detallada del problema")
    @NotBlank(message = "La descripción es requerida")
    private String descripcionDetallada;

    @Schema(description = "Items a devolver")
    @NotEmpty(message = "Debe especificar al menos un item")
    private List<ItemDevolucionRequest> itemsDevolucion;

    @Schema(description = "URLs de fotos como evidencia")
    private List<String> evidenciaFotos;

    @Schema(description = "Método de reembolso preferido", example = "mismo_metodo")
    private String metodoReembolsoPreferido;

    @Schema(description = "Notas adicionales del cliente")
    private String notasCliente;

    @Data
    @Schema(description = "Item a devolver")
    public static class ItemDevolucionRequest {
        
        @Schema(description = "ID del detalle del pedido")
        @NotNull(message = "El detalle del pedido es requerido")
        private UUID detallePedidoId;

        @Schema(description = "Cantidad a devolver")
        @NotNull(message = "La cantidad es requerida")
        private Integer cantidad;

        @Schema(description = "Estado del producto", example = "nuevo")
        private String estadoProducto; // nuevo, usado, defectuoso

        @Schema(description = "Observaciones específicas del item")
        private String observaciones;
    }
}