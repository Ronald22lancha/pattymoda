package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para crear envío
 */
@Data
@Schema(description = "Datos para crear envío")
public class EnvioCreateRequest {

    @Schema(description = "Transportista seleccionado", example = "olva_courier")
    @NotBlank(message = "El transportista es requerido")
    private String transportista;

    @Schema(description = "Tipo de servicio", example = "express")
    private String tipoServicio; // express, regular, economico

    @Schema(description = "Costo del envío", example = "15.00")
    @NotNull(message = "El costo de envío es requerido")
    private BigDecimal costoEnvio;

    @Schema(description = "Peso total del paquete", example = "1.250")
    private BigDecimal pesoTotal;

    @Schema(description = "Dimensiones del paquete en JSON")
    private String dimensiones;

    @Schema(description = "Instrucciones especiales de entrega")
    private String instruccionesEntrega;

    @Schema(description = "Requiere firma del destinatario", example = "true")
    private Boolean requiereFirma = false;

    @Schema(description = "Valor declarado del paquete")
    private BigDecimal valorDeclarado;

    @Schema(description = "Seguro de envío", example = "false")
    private Boolean seguroEnvio = false;

    @Schema(description = "Entrega programada", example = "false")
    private Boolean entregaProgramada = false;

    @Schema(description = "Horario preferido de entrega")
    private String horarioPreferido;
}