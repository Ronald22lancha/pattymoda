package com.dpattymoda.service;

import com.dpattymoda.dto.request.EnvioCreateRequest;
import com.dpattymoda.dto.request.ActualizarEnvioRequest;
import com.dpattymoda.dto.response.EnvioResponse;
import com.dpattymoda.dto.response.SeguimientoEnvioResponse;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de envíos y seguimiento
 */
public interface EnvioService {

    /**
     * Crear envío para pedido
     */
    EnvioResponse crearEnvio(UUID pedidoId, EnvioCreateRequest request);

    /**
     * Obtener envío por ID
     */
    EnvioResponse obtenerEnvio(UUID envioId);

    /**
     * Obtener envío por número de guía
     */
    EnvioResponse obtenerEnvioPorGuia(String numeroGuia);

    /**
     * Actualizar estado de envío
     */
    EnvioResponse actualizarEstadoEnvio(UUID envioId, ActualizarEnvioRequest request);

    /**
     * Obtener seguimiento detallado
     */
    SeguimientoEnvioResponse obtenerSeguimiento(String numeroGuia);

    /**
     * Marcar como entregado
     */
    EnvioResponse marcarComoEntregado(UUID envioId, String evidenciaEntrega);

    /**
     * Registrar intento de entrega fallido
     */
    EnvioResponse registrarIntentoFallido(UUID envioId, String motivo);

    /**
     * Calcular costo de envío
     */
    java.math.BigDecimal calcularCostoEnvio(String ciudadDestino, String departamentoDestino, 
                                           java.math.BigDecimal pesoTotal);

    /**
     * Obtener transportistas disponibles
     */
    List<TransportistaResponse> obtenerTransportistasDisponibles(String ciudadDestino);

    /**
     * Generar etiqueta de envío
     */
    String generarEtiquetaEnvio(UUID envioId);

    /**
     * Sincronizar con API de transportista
     */
    void sincronizarConTransportista(UUID envioId);

    /**
     * Obtener envíos pendientes
     */
    List<EnvioResponse> obtenerEnviosPendientes();

    /**
     * Notificar cliente sobre estado de envío
     */
    void notificarEstadoEnvio(UUID envioId, String nuevoEstado);

    // DTO para transportistas
    record TransportistaResponse(
        String codigo,
        String nombre,
        java.math.BigDecimal costoBase,
        Integer tiempoEstimadoDias,
        boolean disponible
    ) {}
}