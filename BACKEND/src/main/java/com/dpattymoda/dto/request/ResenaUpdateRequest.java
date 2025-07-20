package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * DTO para actualizar reseña
 */
@Data
@Schema(description = "Datos para actualizar reseña")
public class ResenaUpdateRequest {

    @Schema(description = "Calificación del producto", example = "4")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    @Schema(description = "Título de la reseña")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;

    @Schema(description = "Comentario detallado")
    @Size(max = 2000, message = "El comentario no puede exceder 2000 caracteres")
    private String comentario;

    @Schema(description = "Ventajas del producto")
    private List<String> ventajas;

    @Schema(description = "Desventajas del producto")
    private List<String> desventajas;

    @Schema(description = "¿Recomendarías este producto?")
    private Boolean recomendaria;

    @Schema(description = "URLs de imágenes de la reseña")
    private List<String> imagenes;
}