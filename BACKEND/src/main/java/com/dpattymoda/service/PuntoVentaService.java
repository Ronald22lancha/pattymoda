package com.dpattymoda.service;

import com.dpattymoda.dto.request.VentaPresencialRequest;
import com.dpattymoda.dto.request.AperturaCajaRequest;
import com.dpattymoda.dto.request.CierreCajaRequest;
import com.dpattymoda.dto.response.VentaPresencialResponse;
import com.dpattymoda.dto.response.TurnoCajaResponse;
import com.dpattymoda.dto.response.ReporteCajaResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para operaciones del punto de venta
 */
public interface PuntoVentaService {

    /**
     * Abrir turno de caja
     */
    TurnoCajaResponse abrirTurnoCaja(UUID cajaId, AperturaCajaRequest request);

    /**
     * Cerrar turno de caja
     */
    TurnoCajaResponse cerrarTurnoCaja(UUID turnoId, CierreCajaRequest request);

    /**
     * Obtener turno activo de una caja
     */
    TurnoCajaResponse obtenerTurnoActivo(UUID cajaId);

    /**
     * Registrar venta presencial
     */
    VentaPresencialResponse registrarVenta(UUID turnoId, VentaPresencialRequest request);

    /**
     * Procesar pago de venta presencial
     */
    VentaPresencialResponse procesarPago(UUID pedidoId, String metodoPago, String referencia);

    /**
     * Generar comprobante de pago
     */
    String generarComprobante(UUID pedidoId, String tipoComprobante);

    /**
     * Obtener ventas del turno actual
     */
    List<VentaPresencialResponse> obtenerVentasTurno(UUID turnoId);

    /**
     * Generar reporte de caja
     */
    ReporteCajaResponse generarReporteCaja(UUID turnoId);

    /**
     * Obtener resumen de ventas diarias
     */
    ReporteCajaResponse obtenerResumenVentasDiarias(UUID sucursalId, LocalDate fecha);

    /**
     * Validar disponibilidad de stock para venta
     */
    boolean validarStockParaVenta(UUID varianteId, Integer cantidad, UUID sucursalId);

    /**
     * Reservar stock para venta presencial
     */
    void reservarStockVenta(UUID varianteId, Integer cantidad, UUID sucursalId);

    /**
     * Confirmar venta y actualizar inventario
     */
    void confirmarVentaInventario(UUID pedidoId);

    /**
     * Cancelar venta y liberar stock
     */
    void cancelarVentaInventario(UUID pedidoId);
}