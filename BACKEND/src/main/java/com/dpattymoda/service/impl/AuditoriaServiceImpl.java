package com.dpattymoda.service.impl;

import com.dpattymoda.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementación del servicio de auditoría
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuditoriaServiceImpl implements AuditoriaService {

    @Override
    public void registrarAccion(String accion, String tablaAfectada, UUID registroId, 
                               String datosAnteriores, String datosNuevos, String descripcion) {
        // Por ahora solo logueamos, pero aquí se implementaría la inserción en la tabla de auditoría
        log.info("AUDITORIA - Acción: {}, Tabla: {}, Registro: {}, Descripción: {}", 
                accion, tablaAfectada, registroId, descripcion);
    }

    @Override
    public void registrarAccion(UUID usuarioId, String accion, String tablaAfectada, UUID registroId,
                               String datosAnteriores, String datosNuevos, String descripcion) {
        // Por ahora solo logueamos, pero aquí se implementaría la inserción en la tabla de auditoría
        log.info("AUDITORIA - Usuario: {}, Acción: {}, Tabla: {}, Registro: {}, Descripción: {}", 
                usuarioId, accion, tablaAfectada, registroId, descripcion);
    }

    @Override
    public void registrarError(String nivel, String mensaje, String stackTrace, UUID usuarioId) {
        log.error("ERROR_SISTEMA - Nivel: {}, Usuario: {}, Mensaje: {}", nivel, usuarioId, mensaje);
    }

    @Override
    public void registrarMetrica(String nombreMetrica, Double valor, String unidad, String categoria) {
        log.info("METRICA - {}: {} {} ({})", nombreMetrica, valor, unidad, categoria);
    }
}