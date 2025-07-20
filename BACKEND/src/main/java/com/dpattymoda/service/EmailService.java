package com.dpattymoda.service;

/**
 * Servicio para envío de emails
 */
public interface EmailService {

    /**
     * Enviar email de verificación de cuenta
     */
    void enviarEmailVerificacion(String email, String token);

    /**
     * Enviar email de recuperación de contraseña
     */
    void enviarEmailRecuperacion(String email, String token);

    /**
     * Enviar notificación de pedido confirmado
     */
    void enviarNotificacionPedidoConfirmado(String email, String numeroPedido);

    /**
     * Enviar notificación de cambio de estado de pedido
     */
    void enviarNotificacionEstadoPedido(String email, String numeroPedido, String nuevoEstado);

    /**
     * Enviar recordatorio de carrito abandonado
     */
    void enviarRecordatorioCarritoAbandonado(String email, String nombreUsuario);

    /**
     * Enviar newsletter o promociones
     */
    void enviarPromocion(String email, String asunto, String contenido);
}