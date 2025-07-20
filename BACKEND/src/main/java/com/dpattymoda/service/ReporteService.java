package com.dpattymoda.service;

import com.dpattymoda.dto.response.ReporteVentasResponse;
import com.dpattymoda.dto.response.DashboardResponse;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Servicio para generación de reportes y dashboard
 */
public interface ReporteService {

    /**
     * Reporte de ventas por período
     */
    ReporteVentasResponse generarReporteVentas(LocalDate fechaInicio, LocalDate fechaFin, UUID sucursalId);

    /**
     * Dashboard administrativo
     */
    DashboardResponse obtenerDashboard(UUID usuarioId);

    /**
     * Reporte de productos más vendidos
     */
    ReporteVentasResponse.ProductosMasVendidosResponse obtenerProductosMasVendidos(
        LocalDate fechaInicio, LocalDate fechaFin, int limite);

    /**
     * Reporte de ventas por categoría
     */
    ReporteVentasResponse.VentasPorCategoriaResponse obtenerVentasPorCategoria(
        LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Reporte de inventario
     */
    ReporteVentasResponse.ReporteInventarioResponse generarReporteInventario(UUID sucursalId);

    /**
     * Reporte de clientes
     */
    ReporteVentasResponse.ReporteClientesResponse generarReporteClientes(
        LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Exportar reporte a CSV
     */
    byte[] exportarReporteCSV(String tipoReporte, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Exportar reporte a Excel
     */
    byte[] exportarReporteExcel(String tipoReporte, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Reporte de rendimiento de empleados
     */
    ReporteVentasResponse.RendimientoEmpleadosResponse obtenerRendimientoEmpleados(
        LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Análisis de abandono de carrito
     */
    ReporteVentasResponse.AnalisisCarritoResponse analizarAbandonoCarrito(
        LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Métricas de conversión
     */
    ReporteVentasResponse.MetricasConversionResponse obtenerMetricasConversion(
        LocalDate fechaInicio, LocalDate fechaFin);
}