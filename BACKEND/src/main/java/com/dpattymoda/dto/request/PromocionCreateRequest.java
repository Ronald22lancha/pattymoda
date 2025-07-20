package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para crear promoción automática
 */
@Data
@Schema(description = "Datos para crear promoción")
public class PromocionCreateRequest {

    @Schema(description = "Nombre de la promoción", example = "2x1 en Camisas")
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombrePromocion;

    @Schema(description = "Descripción de la promoción")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @Schema(description = "Tipo de promoción", example = "2x1", allowableValues = {"2x1", "3x2", "descuento_cantidad", "descuento_categoria"})
    @NotBlank(message = "El tipo de promoción es requerido")
    private String tipoPromocion;

    @Schema(description = "Condiciones de la promoción en JSON", example = "{\"cantidad_minima\": 2}")
    @NotBlank(message = "Las condiciones son requeridas")
    private String condiciones;

    @Schema(description = "Configuración del descuento en JSON", example = "{\"tipo\": \"porcentaje\", \"valor\": 50}")
    @NotBlank(message = "La configuración del descuento es requerida")
    private String descuento;

    @Schema(description = "Prioridad de aplicación", example = "1")
    @Positive(message = "La prioridad debe ser positiva")
    private Integer prioridad = 1;

    @Schema(description = "Fecha de inicio")
    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDateTime fechaInicio;

    @Schema(description = "Fecha de fin")
    @NotNull(message = "La fecha de fin es requerida")
    private LocalDateTime fechaFin;

    @Schema(description = "Días de la semana aplicables (1=Lunes, 7=Domingo)")
    private List<Integer> diasSemana = List.of(1, 2, 3, 4, 5, 6, 7);

    @Schema(description = "Hora de inicio diaria")
    private LocalTime horasInicio;

    @Schema(description = "Hora de fin diaria")
    private LocalTime horasFin;

    @Schema(description = "IDs de sucursales donde aplica")
    private List<UUID> sucursalesIncluidas;

    @Schema(description = "IDs de categorías incluidas")
    private List<UUID> categoriasIncluidas;

    @Schema(description = "IDs de productos incluidos")
    private List<UUID> productosIncluidos;

    @Schema(description = "Aplicable en ventas online", example = "true")
    private Boolean aplicableOnline = true;

    @Schema(description = "Aplicable en ventas presenciales", example = "true")
    private Boolean aplicablePresencial = true;

    @Schema(description = "Límite de usos totales")
    @Positive(message = "El límite de usos debe ser positivo")
    private Integer limiteUsos;
}