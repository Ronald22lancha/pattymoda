package com.dpattymoda.controller;

import com.dpattymoda.dto.response.ReporteVentasResponse;
import com.dpattymoda.dto.response.DashboardResponse;
import com.dpattymoda.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Controlador para reportes y dashboard
 */
@Tag(name = "Reportes", description = "Reportes y dashboard administrativo")
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Dashboard principal", description = "Obtener datos del dashboard administrativo")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> obtenerDashboard(Authentication authentication) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        DashboardResponse response = reporteService.obtenerDashboard(usuarioId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reporte de ventas", description = "Generar reporte de ventas por período")
    @GetMapping("/ventas")
    public ResponseEntity<ReporteVentasResponse> generarReporteVentas(
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @Parameter(description = "ID de sucursal") 
            @RequestParam(required = false) UUID sucursalId) {
        ReporteVentasResponse response = reporteService.generarReporteVentas(fechaInicio, fechaFin, sucursalId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Productos más vendidos", description = "Reporte de productos más vendidos")
    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<ReporteVentasResponse.ProductosMasVendidosResponse> obtenerProductosMasVendidos(
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @Parameter(description = "Límite de resultados") 
            @RequestParam(defaultValue = "10") int limite) {
        ReporteVentasResponse.ProductosMasVendidosResponse response = 
            reporteService.obtenerProductosMasVendidos(fechaInicio, fechaFin, limite);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Ventas por categoría", description = "Reporte de ventas agrupadas por categoría")
    @GetMapping("/ventas-por-categoria")
    public ResponseEntity<ReporteVentasResponse.VentasPorCategoriaResponse> obtenerVentasPorCategoria(
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        ReporteVentasResponse.VentasPorCategoriaResponse response = 
            reporteService.obtenerVentasPorCategoria(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reporte de inventario", description = "Estado actual del inventario")
    @GetMapping("/inventario")
    public ResponseEntity<ReporteVentasResponse.ReporteInventarioResponse> generarReporteInventario(
            @Parameter(description = "ID de sucursal") @RequestParam(required = false) UUID sucursalId) {
        ReporteVentasResponse.ReporteInventarioResponse response = 
            reporteService.generarReporteInventario(sucursalId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reporte de clientes", description = "Análisis de clientes y comportamiento")
    @GetMapping("/clientes")
    public ResponseEntity<ReporteVentasResponse.ReporteClientesResponse> generarReporteClientes(
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        ReporteVentasResponse.ReporteClientesResponse response = 
            reporteService.generarReporteClientes(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Rendimiento empleados", description = "Reporte de rendimiento de empleados")
    @GetMapping("/rendimiento-empleados")
    public ResponseEntity<ReporteVentasResponse.RendimientoEmpleadosResponse> obtenerRendimientoEmpleados(
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        ReporteVentasResponse.RendimientoEmpleadosResponse response = 
            reporteService.obtenerRendimientoEmpleados(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Análisis carrito", description = "Análisis de abandono de carrito")
    @GetMapping("/analisis-carrito")
    public ResponseEntity<ReporteVentasResponse.AnalisisCarritoResponse> analizarAbandonoCarrito(
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        ReporteVentasResponse.AnalisisCarritoResponse response = 
            reporteService.analizarAbandonoCarrito(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Métricas conversión", description = "Métricas de conversión y rendimiento")
    @GetMapping("/metricas-conversion")
    public ResponseEntity<ReporteVentasResponse.MetricasConversionResponse> obtenerMetricasConversion(
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        ReporteVentasResponse.MetricasConversionResponse response = 
            reporteService.obtenerMetricasConversion(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Exportar CSV", description = "Exportar reporte en formato CSV")
    @GetMapping("/exportar/csv")
    public ResponseEntity<byte[]> exportarReporteCSV(
            @Parameter(description = "Tipo de reporte") @RequestParam String tipoReporte,
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        byte[] csvData = reporteService.exportarReporteCSV(tipoReporte, fechaInicio, fechaFin);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_" + tipoReporte + ".csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csvData);
    }

    @Operation(summary = "Exportar Excel", description = "Exportar reporte en formato Excel")
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarReporteExcel(
            @Parameter(description = "Tipo de reporte") @RequestParam String tipoReporte,
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        byte[] excelData = reporteService.exportarReporteExcel(tipoReporte, fechaInicio, fechaFin);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_" + tipoReporte + ".xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excelData);
    }

    // Método utilitario
    private UUID obtenerUsuarioId(Authentication authentication) {
        return UUID.randomUUID(); // Placeholder
    }
}