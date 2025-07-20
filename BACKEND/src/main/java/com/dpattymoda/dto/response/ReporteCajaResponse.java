package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO para reporte de caja
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reporte detallado de caja")
public class ReporteCajaResponse {

    @Schema(description = "ID del turno")
    private UUID turnoId;

    @Schema(description = "Información de la caja")
    private String nombreCaja;

    @Schema(description = "Sucursal")
    private String nombreSucursal;

    @Schema(description = "Cajero responsable")
    private String nombreCajero;

    @Schema(description = "Fecha del reporte")
    private LocalDate fechaReporte;

    @Schema(description = "Período del reporte")
    private PeriodoReporteResponse periodo;

    @Schema(description = "Resumen financiero")
    private ResumenFinancieroResponse resumenFinanciero;

    @Schema(description = "Ventas por método de pago")
    private Map<String, BigDecimal> ventasPorMetodoPago;

    @Schema(description = "Detalle de transacciones")
    private List<TransaccionResponse> transacciones;

    @Schema(description = "Productos más vendidos")
    private List<ProductoVendidoResponse> productosMasVendidos;

    @Schema(description = "Estadísticas del día")
    private EstadisticasDiaResponse estadisticas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Período del reporte")
    public static class PeriodoReporteResponse {
        
        @Schema(description = "Fecha y hora de inicio")
        private LocalDateTime fechaInicio;

        @Schema(description = "Fecha y hora de fin")
        private LocalDateTime fechaFin;

        @Schema(description = "Duración total")
        private String duracionTotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Resumen financiero")
    public static class ResumenFinancieroResponse {
        
        @Schema(description = "Monto inicial", example = "200.00")
        private BigDecimal montoInicial;

        @Schema(description = "Total ingresos", example = "525.00")
        private BigDecimal totalIngresos;

        @Schema(description = "Total egresos", example = "25.00")
        private BigDecimal totalEgresos;

        @Schema(description = "Monto esperado", example = "700.00")
        private BigDecimal montoEsperado;

        @Schema(description = "Monto real contado", example = "695.00")
        private BigDecimal montoReal;

        @Schema(description = "Diferencia", example = "-5.00")
        private BigDecimal diferencia;

        @Schema(description = "Porcentaje de diferencia", example = "-0.71")
        private BigDecimal porcentajeDiferencia;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detalle de transacción")
    public static class TransaccionResponse {
        
        @Schema(description = "Número de pedido")
        private String numeroPedido;

        @Schema(description = "Hora de la transacción")
        private LocalDateTime hora;

        @Schema(description = "Tipo de movimiento")
        private String tipoMovimiento;

        @Schema(description = "Método de pago")
        private String metodoPago;

        @Schema(description = "Monto")
        private BigDecimal monto;

        @Schema(description = "Cliente")
        private String cliente;

        @Schema(description = "Observaciones")
        private String observaciones;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Producto vendido")
    public static class ProductoVendidoResponse {
        
        @Schema(description = "Nombre del producto")
        private String nombreProducto;

        @Schema(description = "SKU")
        private String sku;

        @Schema(description = "Cantidad vendida")
        private Integer cantidadVendida;

        @Schema(description = "Total vendido")
        private BigDecimal totalVendido;

        @Schema(description = "Precio promedio")
        private BigDecimal precioPromedio;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estadísticas del día")
    public static class EstadisticasDiaResponse {
        
        @Schema(description = "Total de transacciones")
        private Integer totalTransacciones;

        @Schema(description = "Ticket promedio")
        private BigDecimal ticketPromedio;

        @Schema(description = "Transacciones por hora")
        private BigDecimal transaccionesPorHora;

        @Schema(description = "Items vendidos")
        private Integer totalItemsVendidos;

        @Schema(description = "Clientes atendidos")
        private Integer clientesAtendidos;

        @Schema(description = "Tiempo promedio por venta")
        private String tiempoPromedioVenta;
    }
}