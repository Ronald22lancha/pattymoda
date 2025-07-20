package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO para actualizar cantidad en carrito
 */
@Data
@Schema(description = "Datos para actualizar carrito")
public class ActualizarCarritoRequest {

    @Schema(description = "ID de la variante del producto")
    @NotNull(message = "La variante del producto es requerida")
    private UUID varianteId;

    @Schema(description = "Nueva cantidad", example = "3")
    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}