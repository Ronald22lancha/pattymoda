package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO para reporte de ventas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reporte de ventas")
public class ReporteVentasResponse {

    @Schema(description = "Período del reporte")
    private PeriodoReporte periodo;

    @Schema(description = "Resumen general")
    private ResumenGeneral resumen;

    @Schema(description = "Ventas por día")
    private List<VentaDiaria> ventasPorDia;

    @Schema(description = "Ventas por método de pago")
    private Map<String, BigDecimal> ventasPorMetodoPago;

    @Schema(description = "Productos más vendidos")
    private List<ProductoVendido> productosMasVendidos;

    @Schema(description = "Ventas por categoría")
    private List<VentaCategoria> ventasPorCategoria;

    @Schema(description = "Ventas por empleado")
    private List<VentaEmpleado> ventasPorEmpleado;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Período del reporte")
    public static class PeriodoReporte {
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Integer totalDias;
        private String descripcion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Resumen general")
    public static class ResumenGeneral {
        private BigDecimal totalVentas;
        private Integer totalPedidos;
        private BigDecimal ticketPromedio;
        private Integer clientesUnicos;
        private BigDecimal ventasOnline;
        private BigDecimal ventasPresenciales;
        private Integer pedidosCancelados;
        private BigDecimal tasaConversion;
        private BigDecimal crecimientoVentas;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Venta diaria")
    public static class VentaDiaria {
        private LocalDate fecha;
        private BigDecimal totalVentas;
        private Integer totalPedidos;
        private BigDecimal ticketPromedio;
        private Integer clientesUnicos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Producto vendido")
    public static class ProductoVendido {
        private String nombreProducto;
        private String codigoProducto;
        private String categoria;
        private Integer cantidadVendida;
        private BigDecimal totalVentas;
        private BigDecimal precioPromedio;
        private Integer pedidosDiferentes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Venta por categoría")
    public static class VentaCategoria {
        private String nombreCategoria;
        private BigDecimal totalVentas;
        private Integer cantidadProductos;
        private BigDecimal porcentajeTotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Venta por empleado")
    public static class VentaEmpleado {
        private String nombreEmpleado;
        private String rol;
        private BigDecimal totalVentas;
        private Integer totalPedidos;
        private BigDecimal ticketPromedio;
        private BigDecimal comision;
    }

    // DTOs adicionales para otros tipos de reportes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Productos más vendidos")
    public static class ProductosMasVendidosResponse {
        private List<ProductoVendido> productos;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Integer totalProductos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Ventas por categoría")
    public static class VentasPorCategoriaResponse {
        private List<VentaCategoria> categorias;
        private BigDecimal totalGeneral;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Reporte de inventario")
    public static class ReporteInventarioResponse {
        private List<ProductoInventario> productos;
        private Integer totalProductos;
        private Integer productosStockBajo;
        private Integer productosAgotados;
        private BigDecimal valorTotalInventario;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ProductoInventario {
            private String nombreProducto;
            private String sku;
            private Integer stockActual;
            private Integer stockMinimo;
            private Integer stockReservado;
            private BigDecimal valorStock;
            private String estado;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Reporte de clientes")
    public static class ReporteClientesResponse {
        private Integer totalClientes;
        private Integer clientesNuevos;
        private Integer clientesRecurrentes;
        private BigDecimal valorPromedioCliente;
        private List<ClienteTop> clientesTop;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ClienteTop {
            private String nombreCliente;
            private String email;
            private Integer totalPedidos;
            private BigDecimal totalCompras;
            private LocalDate ultimaCompra;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Rendimiento de empleados")
    public static class RendimientoEmpleadosResponse {
        private List<VentaEmpleado> empleados;
        private BigDecimal totalVentasEquipo;
        private BigDecimal promedioVentasEmpleado;
        private String mejorEmpleado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Análisis de carrito")
    public static class AnalisisCarritoResponse {
        private Integer carritosCreados;
        private Integer carritosAbandonados;
        private Integer carritosConvertidos;
        private BigDecimal tasaAbandonoCarrito;
        private BigDecimal valorPromedioCarrito;
        private List<String> motivosAbandonoMasFrecuentes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Métricas de conversión")
    public static class MetricasConversionResponse {
        private Integer visitasUnicas;
        private Integer sesionesConCompra;
        private BigDecimal tasaConversion;
        private BigDecimal valorPromedioSesion;
        private Integer paginasVistasPromedio;
        private String canalMasEfectivo;
    }
}