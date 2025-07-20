package com.dpattymoda.controller;

import com.dpattymoda.dto.request.VentaPresencialRequest;
import com.dpattymoda.dto.request.AperturaCajaRequest;
import com.dpattymoda.dto.request.CierreCajaRequest;
import com.dpattymoda.dto.response.VentaPresencialResponse;
import com.dpattymoda.dto.response.TurnoCajaResponse;
import com.dpattymoda.dto.response.ReporteCajaResponse;
import com.dpattymoda.service.PuntoVentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controlador para punto de venta (POS)
 */
@Tag(name = "Punto de Venta", description = "Operaciones del sistema POS")
@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CAJERO') or hasRole('ADMINISTRADOR')")
public class PuntoVentaController {

    private final PuntoVentaService puntoVentaService;

    @Operation(summary = "Abrir turno de caja", description = "Iniciar un nuevo turno de trabajo en caja")
    @PostMapping("/caja/{cajaId}/abrir")
    public ResponseEntity<TurnoCajaResponse> abrirTurnoCaja(
            @PathVariable UUID cajaId,
            @Valid @RequestBody AperturaCajaRequest request) {
        TurnoCajaResponse response = puntoVentaService.abrirTurnoCaja(cajaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Cerrar turno de caja", description = "Finalizar turno de trabajo y realizar arqueo")
    @PostMapping("/turno/{turnoId}/cerrar")
    public ResponseEntity<TurnoCajaResponse> cerrarTurnoCaja(
            @PathVariable UUID turnoId,
            @Valid @RequestBody CierreCajaRequest request) {
        TurnoCajaResponse response = puntoVentaService.cerrarTurnoCaja(turnoId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener turno activo", description = "Consultar el turno activo de una caja")
    @GetMapping("/caja/{cajaId}/turno-activo")
    public ResponseEntity<TurnoCajaResponse> obtenerTurnoActivo(@PathVariable UUID cajaId) {
        TurnoCajaResponse response = puntoVentaService.obtenerTurnoActivo(cajaId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar venta presencial", description = "Procesar una venta en el punto de venta")
    @PostMapping("/turno/{turnoId}/venta")
    public ResponseEntity<VentaPresencialResponse> registrarVenta(
            @PathVariable UUID turnoId,
            @Valid @RequestBody VentaPresencialRequest request) {
        VentaPresencialResponse response = puntoVentaService.registrarVenta(turnoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Procesar pago", description = "Confirmar el pago de una venta")
    @PostMapping("/pedido/{pedidoId}/procesar-pago")
    public ResponseEntity<VentaPresencialResponse> procesarPago(
            @PathVariable UUID pedidoId,
            @Parameter(description = "Método de pago") @RequestParam String metodoPago,
            @Parameter(description = "Referencia del pago") @RequestParam(required = false) String referencia) {
        VentaPresencialResponse response = puntoVentaService.procesarPago(pedidoId, metodoPago, referencia);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Generar comprobante", description = "Emitir boleta o factura de la venta")
    @PostMapping("/pedido/{pedidoId}/comprobante")
    public ResponseEntity<String> generarComprobante(
            @PathVariable UUID pedidoId,
            @Parameter(description = "Tipo de comprobante") @RequestParam String tipoComprobante) {
        String urlComprobante = puntoVentaService.generarComprobante(pedidoId, tipoComprobante);
        return ResponseEntity.ok(urlComprobante);
    }

    @Operation(summary = "Ventas del turno", description = "Listar todas las ventas del turno actual")
    @GetMapping("/turno/{turnoId}/ventas")
    public ResponseEntity<List<VentaPresencialResponse>> obtenerVentasTurno(@PathVariable UUID turnoId) {
        List<VentaPresencialResponse> response = puntoVentaService.obtenerVentasTurno(turnoId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reporte de caja", description = "Generar reporte detallado del turno")
    @GetMapping("/turno/{turnoId}/reporte")
    public ResponseEntity<ReporteCajaResponse> generarReporteCaja(@PathVariable UUID turnoId) {
        ReporteCajaResponse response = puntoVentaService.generarReporteCaja(turnoId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Resumen diario", description = "Obtener resumen de ventas del día por sucursal")
    @GetMapping("/sucursal/{sucursalId}/resumen-diario")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ReporteCajaResponse> obtenerResumenVentasDiarias(
            @PathVariable UUID sucursalId,
            @Parameter(description = "Fecha del reporte") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        ReporteCajaResponse response = puntoVentaService.obtenerResumenVentasDiarias(sucursalId, fecha);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validar stock", description = "Verificar disponibilidad de stock para venta")
    @GetMapping("/stock/validar")
    public ResponseEntity<Boolean> validarStockParaVenta(
            @Parameter(description = "ID de la variante") @RequestParam UUID varianteId,
            @Parameter(description = "Cantidad requerida") @RequestParam Integer cantidad,
            @Parameter(description = "ID de la sucursal") @RequestParam UUID sucursalId) {
        boolean stockDisponible = puntoVentaService.validarStockParaVenta(varianteId, cantidad, sucursalId);
        return ResponseEntity.ok(stockDisponible);
    }

    @Operation(summary = "Cancelar venta", description = "Cancelar una venta y liberar el stock reservado")
    @PostMapping("/pedido/{pedidoId}/cancelar")
    public ResponseEntity<Void> cancelarVenta(@PathVariable UUID pedidoId) {
        puntoVentaService.cancelarVentaInventario(pedidoId);
        return ResponseEntity.ok().build();
    }
}