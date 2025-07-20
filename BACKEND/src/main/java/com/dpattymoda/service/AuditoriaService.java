package com.dpattymoda.service;

import java.util.UUID;

/**
 * Servicio para auditoría del sistema
 */
public interface AuditoriaService {

    /**
     * Registrar acción de auditoría
     */
    void registrarAccion(String accion, String tablaAfectada, UUID registroId, 
                        String datosAnteriores, String datosNuevos, String descripcion);

    /**
     * Registrar acción con usuario específico
     */
    void registrarAccion(UUID usuarioId, String accion, String tablaAfectada, UUID registroId,
                        String datosAnteriores, String datosNuevos, String descripcion);

    /**
     * Registrar error del sistema
     */
    void registrarError(String nivel, String mensaje, String stackTrace, UUID usuarioId);

    /**
     * Registrar métrica del sistema
     */
    void registrarMetrica(String nombreMetrica, Double valor, String unidad, String categoria);
}