package com.dpattymoda.controller;

import com.dpattymoda.dto.request.ComprobanteCreateRequest;
import com.dpattymoda.dto.response.ComprobanteResponse;
import com.dpattymoda.service.FacturacionElectronicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controlador para facturación electrónica
 */
@Tag(name = "Facturación Electrónica", description = "Integración con SUNAT")
@RestController
@RequestMapping("/api/facturacion")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO') or hasRole('EMPLEADO')")
public class FacturacionController {

    private final FacturacionElectronicaService facturacionService;

    @Operation(summary = "Generar boleta", description = "Generar boleta electrónica")
    @PostMapping("/boleta/{pedidoId}")
    public ResponseEntity<ComprobanteResponse> generarBoleta(
            @PathVariable UUID pedidoId,
            @Valid @RequestBody ComprobanteCreateRequest request) {
        ComprobanteResponse response = facturacionService.generarBoleta(pedidoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Generar factura", description = "Generar factura electrónica")
    @PostMapping("/factura/{pedidoId}")
    public ResponseEntity<ComprobanteResponse> generarFactura(
            @PathVariable UUID pedidoId,
            @Valid @RequestBody ComprobanteCreateRequest request) {
        ComprobanteResponse response = facturacionService.generarFactura(pedidoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Enviar a SUNAT", description = "Enviar comprobante a SUNAT")
    @PostMapping("/{comprobanteId}/enviar-sunat")
    public ResponseEntity<ComprobanteResponse> enviarASunat(@PathVariable UUID comprobanteId) {
        ComprobanteResponse response = facturacionService.enviarASunat(comprobanteId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar estado SUNAT", description = "Consultar estado del comprobante en SUNAT")
    @GetMapping("/{comprobanteId}/estado-sunat")
    public ResponseEntity<ComprobanteResponse> consultarEstadoSunat(@PathVariable UUID comprobanteId) {
        ComprobanteResponse response = facturacionService.consultarEstadoSunat(comprobanteId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Anular comprobante", description = "Anular comprobante electrónico")
    @PostMapping("/{comprobanteId}/anular")
    public ResponseEntity<ComprobanteResponse> anularComprobante(
            @PathVariable UUID comprobanteId,
            @Parameter(description = "Motivo de anulación") @RequestParam String motivo) {
        ComprobanteResponse response = facturacionService.anularComprobante(comprobanteId, motivo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Resumen diario", description = "Generar resumen diario para SUNAT")
    @PostMapping("/resumen-diario")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> generarResumenDiario(
            @Parameter(description = "Fecha del resumen") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        String resumen = facturacionService.generarResumenDiario(fecha);
        return ResponseEntity.ok(resumen);
    }

    @Operation(summary = "Comunicación de baja", description = "Generar comunicación de baja")
    @PostMapping("/comunicacion-baja")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> generarComunicacionBaja(
            @Parameter(description = "Fecha de la comunicación") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "IDs de comprobantes") @RequestParam List<UUID> comprobantesIds) {
        String comunicacion = facturacionService.generarComunicacionBaja(fecha, comprobantesIds);
        return ResponseEntity.ok(comunicacion);
    }

    @Operation(summary = "Descargar XML", description = "Descargar XML firmado del comprobante")
    @GetMapping("/{comprobanteId}/xml")
    public ResponseEntity<String> obtenerXmlFirmado(@PathVariable UUID comprobanteId) {
        String xml = facturacionService.obtenerXmlFirmado(comprobanteId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprobante_" + comprobanteId + ".xml")
            .contentType(MediaType.APPLICATION_XML)
            .body(xml);
    }

    @Operation(summary = "Descargar PDF", description = "Descargar PDF del comprobante")
    @GetMapping("/{comprobanteId}/pdf")
    public ResponseEntity<byte[]> generarPdfComprobante(@PathVariable UUID comprobanteId) {
        byte[] pdfData = facturacionService.generarPdfComprobante(comprobanteId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprobante_" + comprobanteId + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfData);
    }

    @Operation(summary = "Validar RUC", description = "Validar datos de RUC para facturación")
    @GetMapping("/validar-ruc")
    public ResponseEntity<Boolean> validarDatosFacturacion(
            @Parameter(description = "RUC a validar") @RequestParam String ruc,
            @Parameter(description = "Razón social") @RequestParam String razonSocial) {
        boolean valido = facturacionService.validarDatosFacturacion(ruc, razonSocial);
        return ResponseEntity.ok(valido);
    }

    @Operation(summary = "Siguiente número", description = "Obtener siguiente número de serie")
    @GetMapping("/siguiente-numero")
    public ResponseEntity<Integer> obtenerSiguienteNumero(
            @Parameter(description = "Tipo de comprobante") @RequestParam String tipoComprobante,
            @Parameter(description = "Serie") @RequestParam String serie) {
        Integer numero = facturacionService.obtenerSiguienteNumero(tipoComprobante, serie);
        return ResponseEntity.ok(numero);
    }

    @Operation(summary = "Configurar certificado", description = "Configurar certificado digital")
    @PostMapping("/configurar-certificado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> configurarCertificadoDigital(
            @Parameter(description = "Ruta del certificado") @RequestParam String rutaCertificado,
            @Parameter(description = "Password del certificado") @RequestParam String password) {
        facturacionService.configurarCertificadoDigital(rutaCertificado, password);
        return ResponseEntity.ok().build();
    }
}