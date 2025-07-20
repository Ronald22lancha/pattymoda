package com.dpattymoda.service;

import com.dpattymoda.dto.request.ComprobanteCreateRequest;
import com.dpattymoda.dto.response.ComprobanteResponse;

import java.util.UUID;

/**
 * Servicio para facturación electrónica SUNAT
 */
public interface FacturacionElectronicaService {

    /**
     * Generar boleta electrónica
     */
    ComprobanteResponse generarBoleta(UUID pedidoId, ComprobanteCreateRequest request);

    /**
     * Generar factura electrónica
     */
    ComprobanteResponse generarFactura(UUID pedidoId, ComprobanteCreateRequest request);

    /**
     * Enviar comprobante a SUNAT
     */
    ComprobanteResponse enviarASunat(UUID comprobanteId);

    /**
     * Consultar estado en SUNAT
     */
    ComprobanteResponse consultarEstadoSunat(UUID comprobanteId);

    /**
     * Anular comprobante
     */
    ComprobanteResponse anularComprobante(UUID comprobanteId, String motivo);

    /**
     * Generar resumen diario
     */
    String generarResumenDiario(java.time.LocalDate fecha);

    /**
     * Generar comunicación de baja
     */
    String generarComunicacionBaja(java.time.LocalDate fecha, java.util.List<UUID> comprobantesIds);

    /**
     * Obtener XML firmado
     */
    String obtenerXmlFirmado(UUID comprobanteId);

    /**
     * Generar PDF del comprobante
     */
    byte[] generarPdfComprobante(UUID comprobanteId);

    /**
     * Validar datos para facturación
     */
    boolean validarDatosFacturacion(String ruc, String razonSocial);

    /**
     * Obtener siguiente número de serie
     */
    Integer obtenerSiguienteNumero(String tipoComprobante, String serie);

    /**
     * Configurar certificado digital
     */
    void configurarCertificadoDigital(String rutaCertificado, String password);
}