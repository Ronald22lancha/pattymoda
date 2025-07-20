package com.dpattymoda.service;

import com.dpattymoda.dto.request.NotificacionCreateRequest;
import com.dpattymoda.dto.response.NotificacionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de notificaciones
 */
public interface NotificacionService {

    /**
     * Enviar notificación a usuario específico
     */
    NotificacionResponse enviarNotificacion(UUID usuarioId, NotificacionCreateRequest request);

    /**
     * Enviar notificación masiva por rol
     */
    void enviarNotificacionMasiva(String rol, NotificacionCreateRequest request);

    /**
     * Obtener notificaciones del usuario
     */
    Page<NotificacionResponse> obtenerNotificacionesUsuario(UUID usuarioId, Pageable pageable);

    /**
     * Marcar notificación como leída
     */
    void marcarComoLeida(UUID notificacionId, UUID usuarioId);

    /**
     * Marcar todas las notificaciones como leídas
     */
    void marcarTodasComoLeidas(UUID usuarioId);

    /**
     * Obtener contador de notificaciones no leídas
     */
    Integer contarNoLeidas(UUID usuarioId);

    /**
     * Eliminar notificación
     */
    void eliminarNotificacion(UUID notificacionId, UUID usuarioId);

    /**
     * Notificaciones automáticas del sistema
     */
    void notificarPedidoConfirmado(UUID pedidoId);
    void notificarCambioEstadoPedido(UUID pedidoId, String nuevoEstado);
    void notificarStockBajo(UUID productoId);
    void notificarNuevoMensajeChat(UUID chatId, UUID destinatarioId);
    void notificarCarritoAbandonado(UUID carritoId);
    void notificarOfertaEspecial(UUID usuarioId, String codigoCupon);

    /**
     * Configurar preferencias de notificación
     */
    void configurarPreferencias(UUID usuarioId, String tipoNotificacion, boolean habilitado);

    /**
     * Procesar cola de notificaciones pendientes
     */
    void procesarColaPendientes();
}