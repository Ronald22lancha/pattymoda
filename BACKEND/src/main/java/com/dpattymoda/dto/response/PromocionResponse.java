package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO para respuesta de promoción
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de la promoción automática")
public class PromocionResponse {

    @Schema(description = "ID de la promoción")
    private UUID id;

    @Schema(description = "Nombre de la promoción", example = "2x1 en Camisas")
    private String nombrePromocion;

    @Schema(description = "Descripción de la promoción")
    private String descripcion;

    @Schema(description = "Tipo de promoción", example = "2x1")
    private String tipoPromocion;

    @Schema(description = "Condiciones de la promoción")
    private Map<String, Object> condiciones;

    @Schema(description = "Configuración del descuento")
    private Map<String, Object> descuento;

    @Schema(description = "Prioridad de aplicación", example = "1")
    private Integer prioridad;

    @Schema(description = "Fecha de inicio")
    private LocalDateTime fechaInicio;

    @Schema(description = "Fecha de fin")
    private LocalDateTime fechaFin;

    @Schema(description = "Días de la semana aplicables")
    private List<Integer> diasSemana;

    @Schema(description = "Hora de inicio diaria")
    private LocalTime horasInicio;

    @Schema(description = "Hora de fin diaria")
    private LocalTime horasFin;

    @Schema(description = "Sucursales incluidas")
    private List<UUID> sucursalesIncluidas;

    @Schema(description = "Categorías incluidas")
    private List<UUID> categoriasIncluidas;

    @Schema(description = "Productos incluidos")
    private List<UUID> productosIncluidos;

    @Schema(description = "Aplicable online", example = "true")
    private Boolean aplicableOnline;

    @Schema(description = "Aplicable presencial", example = "true")
    private Boolean aplicablePresencial;

    @Schema(description = "Límite de usos")
    private Integer limiteUsos;

    @Schema(description = "Usos actuales", example = "150")
    private Integer usosActuales;

    @Schema(description = "Promoción activa", example = "true")
    private Boolean activa;

    @Schema(description = "Está vigente", example = "true")
    private Boolean vigente;

    @Schema(description = "Puede aplicarse ahora", example = "true")
    private Boolean puedeAplicarse;

    @Schema(description = "Está agotada", example = "false")
    private Boolean agotada;

    @Schema(description = "Usos restantes")
    private Integer usosRestantes;

    @Schema(description = "Fecha de creación")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de actualización")
    private LocalDateTime fechaActualizacion;
}