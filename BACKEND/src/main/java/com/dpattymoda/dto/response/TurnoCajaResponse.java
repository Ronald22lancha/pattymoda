package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para respuesta de turno de caja
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del turno de caja")
public class TurnoCajaResponse {

    @Schema(description = "ID del turno")
    private UUID id;

    @Schema(description = "Información de la caja")
    private CajaInfoResponse caja;

    @Schema(description = "Información del cajero")
    private CajeroInfoResponse cajero;

    @Schema(description = "Fecha de apertura")
    private LocalDateTime fechaApertura;

    @Schema(description = "Fecha de cierre")
    private LocalDateTime fechaCierre;

    @Schema(description = "Monto inicial", example = "200.00")
    private BigDecimal montoInicial;

    @Schema(description = "Monto final", example = "450.00")
    private BigDecimal montoFinal;

    @Schema(description = "Monto esperado", example = "445.00")
    private BigDecimal montoEsperado;

    @Schema(description = "Diferencia (descuadre)", example = "5.00")
    private BigDecimal diferencia;

    @Schema(description = "Total ventas en efectivo", example = "300.00")
    private BigDecimal totalVentasEfectivo;

    @Schema(description = "Total ventas con tarjeta", example = "150.00")
    private BigDecimal totalVentasTarjeta;

    @Schema(description = "Total ventas digitales", example = "75.00")
    private BigDecimal totalVentasDigital;

    @Schema(description = "Total egresos", example = "25.00")
    private BigDecimal totalEgresos;

    @Schema(description = "Número de transacciones", example = "45")
    private Integer numeroTransacciones;

    @Schema(description = "Estado del turno", example = "abierto")
    private String estado;

    @Schema(description = "Observaciones")
    private String observaciones;

    @Schema(description = "Duración del turno", example = "8 horas y 30 minutos")
    private String duracionTurno;

    @Schema(description = "Tiene diferencia", example = "true")
    private Boolean tieneDiferencia;

    @Schema(description = "Está descuadrado", example = "false")
    private Boolean estaDescuadrado;

    @Schema(description = "Detalle del arqueo")
    private String arqueoDetalle;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información de la caja")
    public static class CajaInfoResponse {
        
        @Schema(description = "ID de la caja")
        private UUID id;

        @Schema(description = "Número de caja")
        private String numeroCaja;

        @Schema(description = "Nombre de la caja")
        private String nombreCaja;

        @Schema(description = "Sucursal")
        private String nombreSucursal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información del cajero")
    public static class CajeroInfoResponse {
        
        @Schema(description = "ID del cajero")
        private UUID id;

        @Schema(description = "Nombre completo del cajero")
        private String nombreCompleto;

        @Schema(description = "Email del cajero")
        private String email;
    }
}