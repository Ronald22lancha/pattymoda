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
 * DTO para respuesta de cupón
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del cupón de descuento")
public class CuponResponse {

    @Schema(description = "ID del cupón")
    private UUID id;

    @Schema(description = "Código único del cupón", example = "VERANO2024")
    private String codigoCupon;

    @Schema(description = "Nombre descriptivo", example = "Descuento de Verano")
    private String nombre;

    @Schema(description = "Descripción del cupón")
    private String descripcion;

    @Schema(description = "Tipo de descuento", example = "porcentaje")
    private String tipoDescuento;

    @Schema(description = "Valor del descuento", example = "15.00")
    private BigDecimal valorDescuento;

    @Schema(description = "Monto mínimo de compra", example = "100.00")
    private BigDecimal montoMinimoCompra;

    @Schema(description = "Monto máximo de descuento", example = "50.00")
    private BigDecimal montoMaximoDescuento;

    @Schema(description = "Fecha de inicio de vigencia")
    private LocalDateTime fechaInicio;

    @Schema(description = "Fecha de fin de vigencia")
    private LocalDateTime fechaFin;

    @Schema(description = "Usos máximos totales")
    private Integer usosMaximos;

    @Schema(description = "Usos máximos por usuario", example = "1")
    private Integer usosPorUsuario;

    @Schema(description = "Usos actuales", example = "25")
    private Integer usosActuales;

    @Schema(description = "Solo para primera compra", example = "false")
    private Boolean soloPrimeraCompra;

    @Schema(description = "Aplicable al envío", example = "false")
    private Boolean aplicableEnvio;

    @Schema(description = "Categorías incluidas")
    private List<UUID> categoriasIncluidas;

    @Schema(description = "Productos incluidos")
    private List<UUID> productosIncluidos;

    @Schema(description = "Usuarios específicos")
    private List<UUID> usuariosIncluidos;

    @Schema(description = "Cupón activo", example = "true")
    private Boolean activo;

    @Schema(description = "Código promocional")
    private String codigoPromocional;

    @Schema(description = "Está vigente", example = "true")
    private Boolean vigente;

    @Schema(description = "Puede usarse", example = "true")
    private Boolean puedeUsarse;

    @Schema(description = "Está agotado", example = "false")
    private Boolean agotado;

    @Schema(description = "Usos restantes", example = "75")
    private Integer usosRestantes;

    @Schema(description = "Porcentaje de uso", example = "25.5")
    private Double porcentajeUso;

    @Schema(description = "Fecha de creación")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de actualización")
    private LocalDateTime fechaActualizacion;
}