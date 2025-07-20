package com.dpattymoda.service;

import com.dpattymoda.dto.request.PagoDigitalRequest;
import com.dpattymoda.dto.response.PagoDigitalResponse;

import java.util.UUID;

/**
 * Servicio para pagos con billeteras digitales
 */
public interface PagoDigitalService {

    /**
     * Procesar pago con Yape
     */
    PagoDigitalResponse procesarPagoYape(UUID pedidoId, PagoDigitalRequest request);

    /**
     * Procesar pago con Plin
     */
    PagoDigitalResponse procesarPagoPlin(UUID pedidoId, PagoDigitalRequest request);

    /**
     * Procesar pago con Lukita
     */
    PagoDigitalResponse procesarPagoLukita(UUID pedidoId, PagoDigitalRequest request);

    /**
     * Generar QR para pago digital
     */
    String generarQRPago(UUID pedidoId, String metodoPago);

    /**
     * Verificar estado de pago digital
     */
    PagoDigitalResponse verificarEstadoPago(String referenciaExterna);

    /**
     * Confirmar pago digital
     */
    void confirmarPagoDigital(String referenciaExterna, String estadoPago);

    /**
     * Cancelar pago digital
     */
    void cancelarPagoDigital(String referenciaExterna, String motivo);
}