package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO para agregar producto al carrito
 */
@Data
@Schema(description = "Datos para agregar producto al carrito")
public class AgregarAlCarritoRequest {

    @Schema(description = "ID de la variante del producto")
    @NotNull(message = "La variante del producto es requerida")
    private UUID varianteId;

    @Schema(description = "Cantidad a agregar", example = "2")
    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}