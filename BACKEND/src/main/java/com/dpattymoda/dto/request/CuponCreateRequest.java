package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para crear cupón de descuento
 */
@Data
@Schema(description = "Datos para crear cupón")
public class CuponCreateRequest {

    @Schema(description = "Código único del cupón", example = "VERANO2024")
    @NotBlank(message = "El código del cupón es requerido")
    @Size(min = 3, max = 50, message = "El código debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "El código solo puede contener letras mayúsculas, números, guiones y guiones bajos")
    private String codigoCupon;

    @Schema(description = "Nombre descriptivo del cupón", example = "Descuento de Verano")
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Schema(description = "Descripción del cupón")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @Schema(description = "Tipo de descuento", example = "porcentaje", allowableValues = {"porcentaje", "monto_fijo"})
    @NotBlank(message = "El tipo de descuento es requerido")
    private String tipoDescuento;

    @Schema(description = "Valor del descuento", example = "15.00")
    @NotNull(message = "El valor del descuento es requerido")
    @Positive(message = "El valor del descuento debe ser positivo")
    private BigDecimal valorDescuento;

    @Schema(description = "Monto mínimo de compra", example = "100.00")
    private BigDecimal montoMinimoCompra;

    @Schema(description = "Monto máximo de descuento", example = "50.00")
    private BigDecimal montoMaximoDescuento;

    @Schema(description = "Fecha de inicio de vigencia")
    @NotNull(message = "La fecha de inicio es requerida")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime fechaInicio;

    @Schema(description = "Fecha de fin de vigencia")
    @NotNull(message = "La fecha de fin es requerida")
    private LocalDateTime fechaFin;

    @Schema(description = "Número máximo de usos totales")
    @Positive(message = "Los usos máximos deben ser positivos")
    private Integer usosMaximos;

    @Schema(description = "Usos máximos por usuario", example = "1")
    @Positive(message = "Los usos por usuario deben ser positivos")
    private Integer usosPorUsuario = 1;

    @Schema(description = "Solo para primera compra", example = "false")
    private Boolean soloPrimeraCompra = false;

    @Schema(description = "Aplicable al costo de envío", example = "false")
    private Boolean aplicableEnvio = false;

    @Schema(description = "IDs de categorías incluidas")
    private List<UUID> categoriasIncluidas;

    @Schema(description = "IDs de productos incluidos")
    private List<UUID> productosIncluidos;

    @Schema(description = "IDs de usuarios específicos (cupones personalizados)")
    private List<UUID> usuariosIncluidos;

    @Schema(description = "Código promocional para campañas")
    private String codigoPromocional;
}