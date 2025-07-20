package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para respuesta de reseña
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de la reseña")
public class ResenaResponse {

    @Schema(description = "ID de la reseña")
    private UUID id;

    @Schema(description = "Información del producto")
    private ProductoBasicoResponse producto;

    @Schema(description = "Información del usuario")
    private UsuarioResenaResponse usuario;

    @Schema(description = "Calificación", example = "5")
    private Integer calificacion;

    @Schema(description = "Título de la reseña")
    private String titulo;

    @Schema(description = "Comentario")
    private String comentario;

    @Schema(description = "Ventajas mencionadas")
    private List<String> ventajas;

    @Schema(description = "Desventajas mencionadas")
    private List<String> desventajas;

    @Schema(description = "Recomienda el producto", example = "true")
    private Boolean recomendaria;

    @Schema(description = "Reseña verificada (compra real)", example = "true")
    private Boolean verificada;

    @Schema(description = "Estado de moderación", example = "aprobada")
    private String estadoModeracion;

    @Schema(description = "Fecha de moderación")
    private LocalDateTime fechaModeracion;

    @Schema(description = "Motivo de rechazo")
    private String motivoRechazo;

    @Schema(description = "Utilidad positiva", example = "12")
    private Integer utilidadPositiva;

    @Schema(description = "Utilidad negativa", example = "2")
    private Integer utilidadNegativa;

    @Schema(description = "Número de reportes", example = "0")
    private Integer reportes;

    @Schema(description = "Imágenes de la reseña")
    private List<String> imagenes;

    @Schema(description = "Variante comprada")
    private VarianteCompradaResponse varianteComprada;

    @Schema(description = "Fecha de compra")
    private LocalDateTime fechaCompra;

    @Schema(description = "Fecha de la reseña")
    private LocalDateTime fechaResena;

    @Schema(description = "Fecha de actualización")
    private LocalDateTime fechaActualizacion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Usuario que escribió la reseña")
    public static class UsuarioResenaResponse {
        private String nombreCompleto;
        private String iniciales;
        private Boolean verificado;
        private Integer totalResenas;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Variante comprada")
    public static class VarianteCompradaResponse {
        private UUID id;
        private String sku;
        private String talla;
        private String color;
    }
}