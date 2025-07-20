package com.dpattymoda.service;

import com.dpattymoda.dto.request.DevolucionCreateRequest;
import com.dpattymoda.dto.request.DevolucionUpdateRequest;
import com.dpattymoda.dto.response.DevolucionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de devoluciones y cambios
 */
public interface DevolucionService {

    /**
     * Solicitar devolución
     */
    DevolucionResponse solicitarDevolucion(UUID usuarioId, DevolucionCreateRequest request);

    /**
     * Obtener devolución por ID
     */
    DevolucionResponse obtenerDevolucion(UUID devolucionId);

    /**
     * Listar devoluciones del usuario
     */
    Page<DevolucionResponse> listarDevolucionesUsuario(UUID usuarioId, Pageable pageable);

    /**
     * Listar todas las devoluciones (admin/empleado)
     */
    Page<DevolucionResponse> listarTodasDevoluciones(String estado, Pageable pageable);

    /**
     * Aprobar devolución
     */
    DevolucionResponse aprobarDevolucion(UUID devolucionId, UUID empleadoId, String observaciones);

    /**
     * Rechazar devolución
     */
    DevolucionResponse rechazarDevolucion(UUID devolucionId, UUID empleadoId, String motivo);

    /**
     * Procesar devolución (recepción de items)
     */
    DevolucionResponse procesarDevolucion(UUID devolucionId, DevolucionUpdateRequest request);

    /**
     * Completar devolución
     */
    DevolucionResponse completarDevolucion(UUID devolucionId, String metodoReembolso);

    /**
     * Cancelar devolución
     */
    void cancelarDevolucion(UUID devolucionId, UUID usuarioId, String motivo);

    /**
     * Obtener estadísticas de devoluciones
     */
    EstadisticasDevolucionResponse obtenerEstadisticas();

    /**
     * Generar etiqueta de envío para devolución
     */
    String generarEtiquetaEnvio(UUID devolucionId);

    /**
     * Validar elegibilidad para devolución
     */
    boolean validarElegibilidadDevolucion(UUID pedidoId, List<UUID> itemsDevolucion);

    // DTO para estadísticas
    record EstadisticasDevolucionResponse(
        Integer totalDevoluciones,
        Integer devolucionesPendientes,
        Integer devolucionesAprobadas,
        Integer devolucionesRechazadas,
        Double porcentajeDevolucion,
        String motivoMasFrecuente
    ) {}
}