package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO para crear reseña de producto
 */
@Data
@Schema(description = "Datos para crear reseña")
public class ResenaCreateRequest {

    @Schema(description = "ID del producto a reseñar")
    @NotNull(message = "El producto es requerido")
    private UUID productoId;

    @Schema(description = "ID del pedido (para verificación)", required = false)
    private UUID pedidoId;

    @Schema(description = "Calificación del producto", example = "5")
    @NotNull(message = "La calificación es requerida")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    @Schema(description = "Título de la reseña", example = "Excelente calidad")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;

    @Schema(description = "Comentario detallado")
    @Size(max = 2000, message = "El comentario no puede exceder 2000 caracteres")
    private String comentario;

    @Schema(description = "Ventajas del producto")
    private List<String> ventajas;

    @Schema(description = "Desventajas del producto")
    private List<String> desventajas;

    @Schema(description = "¿Recomendarías este producto?", example = "true")
    private Boolean recomendaria;

    @Schema(description = "URLs de imágenes de la reseña")
    private List<String> imagenes;

    @Schema(description = "ID de la variante comprada")
    private UUID varianteCompradaId;

    @Schema(description = "Talla comprada", example = "M")
    private String tallaComprada;

    @Schema(description = "Color comprado", example = "Azul")
    private String colorComprado;
}