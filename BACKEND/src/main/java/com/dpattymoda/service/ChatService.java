package com.dpattymoda.service;

import com.dpattymoda.dto.request.ChatCreateRequest;
import com.dpattymoda.dto.request.MensajeCreateRequest;
import com.dpattymoda.dto.response.ChatResponse;
import com.dpattymoda.dto.response.MensajeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para chat en tiempo real
 */
public interface ChatService {

    /**
     * Crear nuevo chat
     */
    ChatResponse crearChat(UUID usuarioId, ChatCreateRequest request);

    /**
     * Obtener chat por ID
     */
    ChatResponse obtenerChat(UUID chatId);

    /**
     * Listar chats del usuario
     */
    Page<ChatResponse> listarChatsUsuario(UUID usuarioId, Pageable pageable);

    /**
     * Listar chats asignados al empleado
     */
    Page<ChatResponse> listarChatsEmpleado(UUID empleadoId, Pageable pageable);

    /**
     * Listar chats pendientes de asignación
     */
    List<ChatResponse> listarChatsPendientes();

    /**
     * Asignar chat a empleado
     */
    ChatResponse asignarChatAEmpleado(UUID chatId, UUID empleadoId);

    /**
     * Enviar mensaje
     */
    MensajeResponse enviarMensaje(UUID chatId, UUID remitenteId, MensajeCreateRequest request);

    /**
     * Obtener mensajes del chat
     */
    List<MensajeResponse> obtenerMensajesChat(UUID chatId);

    /**
     * Marcar mensajes como leídos
     */
    void marcarMensajesComoLeidos(UUID chatId, UUID usuarioId);

    /**
     * Cerrar chat
     */
    ChatResponse cerrarChat(UUID chatId, UUID usuarioId, String motivoCierre);

    /**
     * Calificar atención del chat
     */
    void calificarChat(UUID chatId, Integer satisfaccion, String comentario);

    /**
     * Obtener chats activos para notificaciones
     */
    List<ChatResponse> obtenerChatsActivos();

    /**
     * Escalar chat a supervisor
     */
    ChatResponse escalarChat(UUID chatId, String motivo);
}