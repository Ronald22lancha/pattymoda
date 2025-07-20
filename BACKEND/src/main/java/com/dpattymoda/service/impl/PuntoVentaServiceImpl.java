package com.dpattymoda.service.impl;

import com.dpattymoda.dto.request.VentaPresencialRequest;
import com.dpattymoda.dto.request.AperturaCajaRequest;
import com.dpattymoda.dto.request.CierreCajaRequest;
import com.dpattymoda.dto.response.VentaPresencialResponse;
import com.dpattymoda.dto.response.TurnoCajaResponse;
import com.dpattymoda.dto.response.ReporteCajaResponse;
import com.dpattymoda.entity.*;
import com.dpattymoda.exception.BusinessException;
import com.dpattymoda.exception.ResourceNotFoundException;
import com.dpattymoda.repository.*;
import com.dpattymoda.service.AuditoriaService;
import com.dpattymoda.service.PuntoVentaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de punto de venta
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PuntoVentaServiceImpl implements PuntoVentaService {

    private final TurnoCajaRepository turnoCajaRepository;
    private final CajaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final InventarioRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    @Override
    public TurnoCajaResponse abrirTurnoCaja(UUID cajaId, AperturaCajaRequest request) {
        log.info("Abriendo turno de caja ID: {}", cajaId);

        // Obtener caja
        Caja caja = cajaRepository.findById(cajaId)
            .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada"));

        if (!caja.estaActiva()) {
            throw new BusinessException("La caja no está activa");
        }

        // Verificar que no haya turno abierto
        if (caja.tieneTurnoAbierto()) {
            throw new BusinessException("Ya existe un turno abierto para esta caja");
        }

        // Obtener usuario actual
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario cajero = usuarioRepository.findByEmailAndActivoTrue(emailUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!cajero.esCajero()) {
            throw new BusinessException("El usuario no tiene permisos de cajero");
        }

        // Crear turno
        TurnoCaja turno = TurnoCaja.builder()
            .caja(caja)
            .cajero(cajero)
            .montoInicial(request.getMontoInicial())
            .observaciones(request.getObservaciones())
            .arqueoDetalle(request.getArqueoDetalle())
            .estado("abierto")
            .build();

        turno = turnoCajaRepository.save(turno);

        // Registrar movimiento inicial
        if (request.getMontoInicial().compareTo(BigDecimal.ZERO) > 0) {
            MovimientoCaja movimientoInicial = MovimientoCaja.builder()
                .turnoCaja(turno)
                .tipoMovimiento("ingreso_extra")
                .concepto("Apertura de caja - Monto inicial")
                .monto(request.getMontoInicial())
                .metodoPago("efectivo")
                .build();

            movimientoCajaRepository.save(movimientoInicial);
        }

        // Auditar apertura
        auditoriaService.registrarAccion("APERTURA_CAJA", "turnos_caja", turno.getId(),
            null, convertirAJson(turno), "Apertura de turno de caja: " + caja.getNombreCaja());

        log.info("Turno de caja abierto exitosamente: {}", turno.getId());
        return convertirATurnoCajaResponse(turno);
    }

    @Override
    public TurnoCajaResponse cerrarTurnoCaja(UUID turnoId, CierreCajaRequest request) {
        log.info("Cerrando turno de caja ID: {}", turnoId);

        TurnoCaja turno = turnoCajaRepository.findById(turnoId)
            .orElseThrow(() -> new ResourceNotFoundException("Turno de caja no encontrado"));

        if (!turno.estaAbierto()) {
            throw new BusinessException("El turno de caja ya está cerrado");
        }

        // Verificar que el usuario actual sea el cajero del turno o un supervisor
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(emailUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!turno.getCajero().getId().equals(usuario.getId()) && !usuario.esAdministrador()) {
            throw new BusinessException("Solo el cajero del turno o un administrador puede cerrarlo");
        }

        // Cerrar turno
        turno.cerrarTurno(request.getMontoFinal(), request.getObservaciones());
        if (request.getArqueoDetalle() != null) {
            turno.setArqueoDetalle(request.getArqueoDetalle());
        }

        turno = turnoCajaRepository.save(turno);

        // Auditar cierre
        auditoriaService.registrarAccion("CIERRE_CAJA", "turnos_caja", turno.getId(),
            null, convertirAJson(turno), "Cierre de turno de caja: " + turno.getCaja().getNombreCaja());

        log.info("Turno de caja cerrado exitosamente: {}", turno.getId());
        return convertirATurnoCajaResponse(turno);
    }

    @Override
    @Transactional(readOnly = true)
    public TurnoCajaResponse obtenerTurnoActivo(UUID cajaId) {
        Caja caja = cajaRepository.findById(cajaId)
            .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada"));

        TurnoCaja turnoActivo = caja.getTurnoActual();
        if (turnoActivo == null) {
            throw new ResourceNotFoundException("No hay turno activo para esta caja");
        }

        return convertirATurnoCajaResponse(turnoActivo);
    }

    @Override
    public VentaPresencialResponse registrarVenta(UUID turnoId, VentaPresencialRequest request) {
        log.info("Registrando venta presencial en turno: {}", turnoId);

        // Validar turno activo
        TurnoCaja turno = turnoCajaRepository.findById(turnoId)
            .orElseThrow(() -> new ResourceNotFoundException("Turno de caja no encontrado"));

        if (!turno.estaAbierto()) {
            throw new BusinessException("El turno de caja no está abierto");
        }

        // Validar stock disponible
        for (VentaPresencialRequest.ItemVentaRequest item : request.getItems()) {
            if (!validarStockParaVenta(item.getVarianteId(), item.getCantidad(), 
                                     turno.getCaja().getSucursal().getId())) {
                VarianteProducto variante = varianteProductoRepository.findById(item.getVarianteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
                throw new BusinessException("Stock insuficiente para: " + variante.getNombreCompleto());
            }
        }

        // Crear pedido
        Pedido pedido = crearPedidoPresencial(turno, request);

        // Reservar stock
        for (VentaPresencialRequest.ItemVentaRequest item : request.getItems()) {
            reservarStockVenta(item.getVarianteId(), item.getCantidad(), 
                             turno.getCaja().getSucursal().getId());
        }

        // Registrar movimiento de caja
        MovimientoCaja movimiento = MovimientoCaja.builder()
            .turnoCaja(turno)
            .pedido(pedido)
            .tipoMovimiento("venta")
            .concepto("Venta presencial - " + pedido.getNumeroPedido())
            .monto(pedido.getTotal())
            .metodoPago(request.getMetodoPago())
            .referencia(request.getReferenciaPago())
            .build();

        movimientoCajaRepository.save(movimiento);

        // Auditar venta
        auditoriaService.registrarAccion("VENTA_PRESENCIAL", "pedidos", pedido.getId(),
            null, convertirAJson(pedido), "Venta presencial registrada: " + pedido.getNumeroPedido());

        log.info("Venta presencial registrada exitosamente: {}", pedido.getNumeroPedido());
        return convertirAVentaPresencialResponse(pedido, turno);
    }

    @Override
    public VentaPresencialResponse procesarPago(UUID pedidoId, String metodoPago, String referencia) {
        log.info("Procesando pago para pedido: {}", pedidoId);

        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (!"pendiente".equals(pedido.getEstadoPago())) {
            throw new BusinessException("El pedido ya tiene el pago procesado");
        }

        // Actualizar estado de pago
        pedido.setEstadoPago("procesado");
        pedido.setEstado("confirmado");
        pedidoRepository.save(pedido);

        // Confirmar venta en inventario
        confirmarVentaInventario(pedidoId);

        // Auditar procesamiento de pago
        auditoriaService.registrarAccion("PROCESAR_PAGO", "pedidos", pedido.getId(),
            null, null, "Pago procesado para pedido: " + pedido.getNumeroPedido());

        log.info("Pago procesado exitosamente para pedido: {}", pedido.getNumeroPedido());
        
        // Obtener turno para la respuesta
        TurnoCaja turno = turnoCajaRepository.findById(pedido.getCajaId())
            .orElse(null);
        
        return convertirAVentaPresencialResponse(pedido, turno);
    }

    @Override
    public String generarComprobante(UUID pedidoId, String tipoComprobante) {
        log.info("Generando comprobante {} para pedido: {}", tipoComprobante, pedidoId);

        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        // Aquí se implementaría la generación del comprobante
        // Por ahora retornamos una URL simulada
        String numeroComprobante = generarNumeroComprobante(tipoComprobante);
        String urlComprobante = "/api/comprobantes/" + numeroComprobante + ".pdf";

        // Actualizar pedido con datos del comprobante
        pedido.setTipoComprobante(tipoComprobante);
        pedidoRepository.save(pedido);

        log.info("Comprobante generado: {}", numeroComprobante);
        return urlComprobante;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaPresencialResponse> obtenerVentasTurno(UUID turnoId) {
        TurnoCaja turno = turnoCajaRepository.findById(turnoId)
            .orElseThrow(() -> new ResourceNotFoundException("Turno de caja no encontrado"));

        List<MovimientoCaja> movimientos = movimientoCajaRepository.findByTurnoCaja_IdAndTipoMovimiento(
            turnoId, "venta");

        return movimientos.stream()
            .filter(m -> m.getPedido() != null)
            .map(m -> convertirAVentaPresencialResponse(m.getPedido(), turno))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteCajaResponse generarReporteCaja(UUID turnoId) {
        TurnoCaja turno = turnoCajaRepository.findById(turnoId)
            .orElseThrow(() -> new ResourceNotFoundException("Turno de caja no encontrado"));

        return generarReporteDetallado(turno);
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteCajaResponse obtenerResumenVentasDiarias(UUID sucursalId, LocalDate fecha) {
        // Implementar lógica para resumen diario por sucursal
        // Por ahora retornamos un reporte básico
        return ReporteCajaResponse.builder()
            .fechaReporte(fecha)
            .nombreSucursal("Sucursal Principal")
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarStockParaVenta(UUID varianteId, Integer cantidad, UUID sucursalId) {
        Integer stockDisponible = inventarioRepository.obtenerStockDisponiblePorVariante(varianteId);
        return stockDisponible != null && stockDisponible >= cantidad;
    }

    @Override
    public void reservarStockVenta(UUID varianteId, Integer cantidad, UUID sucursalId) {
        Inventario inventario = inventarioRepository.findByVariante_IdAndSucursal_Id(varianteId, sucursalId)
            .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado"));

        inventario.reservarStock(cantidad);
        inventarioRepository.save(inventario);
    }

    @Override
    public void confirmarVentaInventario(UUID pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        for (DetallePedido detalle : pedido.getDetalles()) {
            Inventario inventario = inventarioRepository
                .findByVariante_IdAndSucursal_Id(detalle.getVariante().getId(), pedido.getSucursal().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado"));

            inventario.confirmarVenta(detalle.getCantidad());
            inventarioRepository.save(inventario);
        }
    }

    @Override
    public void cancelarVentaInventario(UUID pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        for (DetallePedido detalle : pedido.getDetalles()) {
            Inventario inventario = inventarioRepository
                .findByVariante_IdAndSucursal_Id(detalle.getVariante().getId(), pedido.getSucursal().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado"));

            inventario.liberarStock(detalle.getCantidad());
            inventarioRepository.save(inventario);
        }
    }

    // Métodos privados de utilidad

    private Pedido crearPedidoPresencial(TurnoCaja turno, VentaPresencialRequest request) {
        // Crear pedido
        Pedido pedido = Pedido.builder()
            .sucursal(turno.getCaja().getSucursal())
            .vendedor(turno.getCajero())
            .cajaId(turno.getCaja().getId())
            .tipoVenta("presencial")
            .estado("pendiente")
            .metodoPago(request.getMetodoPago())
            .estadoPago("pendiente")
            .descuentoTotal(request.getDescuentoTotal())
            .notasCliente(request.getNotas())
            .comprobanteRequerido(request.getRequiereComprobante())
            .tipoComprobante(request.getTipoComprobante())
            .datosCliente(convertirAJson(request.getDatosCliente()))
            .datosFacturacion(convertirAJson(request.getDatosFacturacion()))
            .build();

        pedido = pedidoRepository.save(pedido);

        // Crear detalles
        BigDecimal subtotal = BigDecimal.ZERO;
        for (VentaPresencialRequest.ItemVentaRequest item : request.getItems()) {
            VarianteProducto variante = varianteProductoRepository.findById(item.getVarianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));

            DetallePedido detalle = DetallePedido.builder()
                .pedido(pedido)
                .variante(variante)
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .descuentoUnitario(item.getDescuentoUnitario())
                .datosProducto(convertirAJson(variante))
                .build();

            detallePedidoRepository.save(detalle);
            subtotal = subtotal.add(detalle.getSubtotal());
        }

        // Calcular totales
        pedido.setSubtotal(subtotal);
        pedido.calcularTotales();
        
        return pedidoRepository.save(pedido);
    }

    private TurnoCajaResponse convertirATurnoCajaResponse(TurnoCaja turno) {
        return TurnoCajaResponse.builder()
            .id(turno.getId())
            .caja(TurnoCajaResponse.CajaInfoResponse.builder()
                .id(turno.getCaja().getId())
                .numeroCaja(turno.getCaja().getNumeroCaja())
                .nombreCaja(turno.getCaja().getNombreCaja())
                .nombreSucursal(turno.getCaja().getSucursal().getNombreSucursal())
                .build())
            .cajero(TurnoCajaResponse.CajeroInfoResponse.builder()
                .id(turno.getCajero().getId())
                .nombreCompleto(turno.getCajero().getNombreCompleto())
                .email(turno.getCajero().getEmail())
                .build())
            .fechaApertura(turno.getFechaApertura())
            .fechaCierre(turno.getFechaCierre())
            .montoInicial(turno.getMontoInicial())
            .montoFinal(turno.getMontoFinal())
            .montoEsperado(turno.getMontoEsperado())
            .diferencia(turno.getDiferencia())
            .totalVentasEfectivo(turno.getTotalVentasEfectivo())
            .totalVentasTarjeta(turno.getTotalVentasTarjeta())
            .totalVentasDigital(turno.getTotalVentasDigital())
            .totalEgresos(turno.getTotalEgresos())
            .numeroTransacciones(turno.getNumeroTransacciones())
            .estado(turno.getEstado())
            .observaciones(turno.getObservaciones())
            .duracionTurno(turno.getDuracionTurno())
            .tieneDiferencia(turno.tieneDiferencia())
            .estaDescuadrado(turno.estaDescuadrado())
            .arqueoDetalle(turno.getArqueoDetalle())
            .build();
    }

    private VentaPresencialResponse convertirAVentaPresencialResponse(Pedido pedido, TurnoCaja turno) {
        // Calcular cambio para efectivo
        BigDecimal cambio = BigDecimal.ZERO;
        if ("efectivo".equals(pedido.getMetodoPago())) {
            // Aquí se obtendría el monto recibido del movimiento de caja
            cambio = BigDecimal.ZERO; // Placeholder
        }

        return VentaPresencialResponse.builder()
            .id(pedido.getId())
            .numeroPedido(pedido.getNumeroPedido())
            .vendedor(VentaPresencialResponse.VendedorResponse.builder()
                .id(pedido.getVendedor().getId())
                .nombreCompleto(pedido.getVendedor().getNombreCompleto())
                .email(pedido.getVendedor().getEmail())
                .build())
            .caja(turno != null ? VentaPresencialResponse.CajaResponse.builder()
                .id(turno.getCaja().getId())
                .numeroCaja(turno.getCaja().getNumeroCaja())
                .nombreCaja(turno.getCaja().getNombreCaja())
                .nombreSucursal(turno.getCaja().getSucursal().getNombreSucursal())
                .build() : null)
            .subtotal(pedido.getSubtotal())
            .descuentoTotal(pedido.getDescuentoTotal())
            .impuestosTotal(pedido.getImpuestosTotal())
            .total(pedido.getTotal())
            .metodoPago(pedido.getMetodoPago())
            .estadoPago(pedido.getEstadoPago())
            .cambio(cambio)
            .comprobanteRequerido(pedido.getComprobanteRequerido())
            .tipoComprobante(pedido.getTipoComprobante())
            .notas(pedido.getNotasCliente())
            .fechaVenta(pedido.getFechaCreacion())
            .build();
    }

    private ReporteCajaResponse generarReporteDetallado(TurnoCaja turno) {
        return ReporteCajaResponse.builder()
            .turnoId(turno.getId())
            .nombreCaja(turno.getCaja().getNombreCaja())
            .nombreSucursal(turno.getCaja().getSucursal().getNombreSucursal())
            .nombreCajero(turno.getCajero().getNombreCompleto())
            .fechaReporte(turno.getFechaApertura().toLocalDate())
            .periodo(ReporteCajaResponse.PeriodoReporteResponse.builder()
                .fechaInicio(turno.getFechaApertura())
                .fechaFin(turno.getFechaCierre())
                .duracionTotal(turno.getDuracionTurno())
                .build())
            .resumenFinanciero(ReporteCajaResponse.ResumenFinancieroResponse.builder()
                .montoInicial(turno.getMontoInicial())
                .totalIngresos(turno.getTotalVentas())
                .totalEgresos(turno.getTotalEgresos())
                .montoEsperado(turno.getMontoEsperado())
                .montoReal(turno.getMontoFinal())
                .diferencia(turno.getDiferencia())
                .build())
            .build();
    }

    private String generarNumeroComprobante(String tipoComprobante) {
        String serie = "boleta".equals(tipoComprobante) ? "B001" : "F001";
        // Aquí se implementaría la lógica para obtener el siguiente número
        int numero = 1; // Placeholder
        return serie + "-" + String.format("%08d", numero);
    }

    private String convertirAJson(Object objeto) {
        if (objeto == null) return null;
        try {
            return objectMapper.writeValueAsString(objeto);
        } catch (JsonProcessingException e) {
            log.warn("Error al convertir objeto a JSON: {}", e.getMessage());
            return null;
        }
    }
}