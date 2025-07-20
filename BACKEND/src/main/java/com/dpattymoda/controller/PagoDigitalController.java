package com.dpattymoda.controller;

import com.dpattymoda.dto.request.PagoDigitalRequest;
import com.dpattymoda.dto.response.PagoDigitalResponse;
import com.dpattymoda.service.PagoDigitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador para pagos con billeteras digitales
 */
@Tag(name = "Pagos Digitales", description = "Procesamiento de pagos con Yape, Plin, Lukita")
@RestController
@RequestMapping("/api/pagos-digitales")
@RequiredArgsConstructor
public class PagoDigitalController {

    private final PagoDigitalService pagoDigitalService;

    @Operation(summary = "Pagar con Yape", description = "Procesar pago usando Yape")
    @PostMapping("/yape/{pedidoId}")
    public ResponseEntity<PagoDigitalResponse> pagarConYape(
            @PathVariable UUID pedidoId,
            @Valid @RequestBody PagoDigitalRequest request) {
        PagoDigitalResponse response = pagoDigitalService.procesarPagoYape(pedidoId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Pagar con Plin", description = "Procesar pago usando Plin")
    @PostMapping("/plin/{pedidoId}")
    public ResponseEntity<PagoDigitalResponse> pagarConPlin(
            @PathVariable UUID pedidoId,
            @Valid @RequestBody PagoDigitalRequest request) {
        PagoDigitalResponse response = pagoDigitalService.procesarPagoPlin(pedidoId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Pagar con Lukita", description = "Procesar pago usando Lukita")
    @PostMapping("/lukita/{pedidoId}")
    public ResponseEntity<PagoDigitalResponse> pagarConLukita(
            @PathVariable UUID pedidoId,
            @Valid @RequestBody PagoDigitalRequest request) {
        PagoDigitalResponse response = pagoDigitalService.procesarPagoLukita(pedidoId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Generar QR", description = "Generar código QR para pago digital")
    @GetMapping("/qr/{pedidoId}")
    public ResponseEntity<String> generarQRPago(
            @PathVariable UUID pedidoId,
            @Parameter(description = "Método de pago") @RequestParam String metodoPago) {
        String urlQR = pagoDigitalService.generarQRPago(pedidoId, metodoPago);
        return ResponseEntity.ok(urlQR);
    }

    @Operation(summary = "Verificar estado", description = "Consultar estado de pago digital")
    @GetMapping("/estado/{referenciaExterna}")
    public ResponseEntity<PagoDigitalResponse> verificarEstadoPago(
            @PathVariable String referenciaExterna) {
        PagoDigitalResponse response = pagoDigitalService.verificarEstadoPago(referenciaExterna);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Confirmar pago", description = "Webhook para confirmar pago desde billetera")
    @PostMapping("/confirmar/{referenciaExterna}")
    public ResponseEntity<Void> confirmarPago(
            @PathVariable String referenciaExterna,
            @Parameter(description = "Estado del pago") @RequestParam String estadoPago) {
        pagoDigitalService.confirmarPagoDigital(referenciaExterna, estadoPago);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cancelar pago", description = "Cancelar pago digital pendiente")
    @PostMapping("/cancelar/{referenciaExterna}")
    public ResponseEntity<Void> cancelarPago(
            @PathVariable String referenciaExterna,
            @Parameter(description = "Motivo de cancelación") @RequestParam String motivo) {
        pagoDigitalService.cancelarPagoDigital(referenciaExterna, motivo);
        return ResponseEntity.ok().build();
    }
}