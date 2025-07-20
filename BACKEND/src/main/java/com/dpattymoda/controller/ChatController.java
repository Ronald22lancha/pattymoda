package com.dpattymoda.controller;

import com.dpattymoda.dto.request.ChatCreateRequest;
import com.dpattymoda.dto.request.MensajeCreateRequest;
import com.dpattymoda.dto.response.ChatResponse;
import com.dpattymoda.dto.response.MensajeResponse;
import com.dpattymoda.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador para chat en tiempo real
 */
@Tag(name = "Chat", description = "Sistema de chat en tiempo real")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Crear nuevo chat", description = "Iniciar conversación con soporte")
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ChatResponse> crearChat(
            Authentication authentication,
            @Valid @RequestBody ChatCreateRequest request) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        ChatResponse response = chatService.crearChat(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener chat", description = "Consultar información de un chat")
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> obtenerChat(@PathVariable UUID chatId) {
        ChatResponse response = chatService.obtenerChat(chatId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mis chats", description = "Listar chats del usuario autenticado")
    @GetMapping("/mis-chats")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Page<ChatResponse>> listarMisChats(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        Page<ChatResponse> response = chatService.listarChatsUsuario(usuarioId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Chats asignados", description = "Listar chats asignados al empleado")
    @GetMapping("/asignados")
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<ChatResponse>> listarChatsAsignados(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID empleadoId = obtenerUsuarioId(authentication);
        Page<ChatResponse> response = chatService.listarChatsEmpleado(empleadoId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Chats pendientes", description = "Listar chats sin asignar")
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<ChatResponse>> listarChatsPendientes() {
        List<ChatResponse> response = chatService.listarChatsPendientes();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Asignar chat", description = "Asignar chat a empleado")
    @PostMapping("/{chatId}/asignar")
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ChatResponse> asignarChat(
            @PathVariable UUID chatId,
            Authentication authentication,
            @Parameter(description = "ID del empleado") @RequestParam(required = false) UUID empleadoId) {
        
        // Si no se especifica empleado, asignar al usuario actual
        if (empleadoId == null) {
            empleadoId = obtenerUsuarioId(authentication);
        }
        
        ChatResponse response = chatService.asignarChatAEmpleado(chatId, empleadoId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Enviar mensaje", description = "Enviar mensaje en el chat")
    @PostMapping("/{chatId}/mensajes")
    public ResponseEntity<MensajeResponse> enviarMensaje(
            @PathVariable UUID chatId,
            Authentication authentication,
            @Valid @RequestBody MensajeCreateRequest request) {
        UUID remitenteId = obtenerUsuarioId(authentication);
        MensajeResponse response = chatService.enviarMensaje(chatId, remitenteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener mensajes", description = "Listar mensajes del chat")
    @GetMapping("/{chatId}/mensajes")
    public ResponseEntity<List<MensajeResponse>> obtenerMensajes(@PathVariable UUID chatId) {
        List<MensajeResponse> response = chatService.obtenerMensajesChat(chatId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Marcar como leído", description = "Marcar mensajes como leídos")
    @PostMapping("/{chatId}/marcar-leido")
    public ResponseEntity<Void> marcarComoLeido(
            @PathVariable UUID chatId,
            Authentication authentication) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        chatService.marcarMensajesComoLeidos(chatId, usuarioId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cerrar chat", description = "Finalizar conversación")
    @PostMapping("/{chatId}/cerrar")
    public ResponseEntity<ChatResponse> cerrarChat(
            @PathVariable UUID chatId,
            Authentication authentication,
            @Parameter(description = "Motivo de cierre") @RequestParam String motivo) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        ChatResponse response = chatService.cerrarChat(chatId, usuarioId, motivo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Calificar atención", description = "Calificar la atención recibida")
    @PostMapping("/{chatId}/calificar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> calificarChat(
            @PathVariable UUID chatId,
            @Parameter(description = "Satisfacción (1-5)") @RequestParam Integer satisfaccion,
            @Parameter(description = "Comentario") @RequestParam(required = false) String comentario) {
        chatService.calificarChat(chatId, satisfaccion, comentario);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Escalar chat", description = "Escalar chat a supervisor")
    @PostMapping("/{chatId}/escalar")
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ChatResponse> escalarChat(
            @PathVariable UUID chatId,
            @Parameter(description = "Motivo de escalamiento") @RequestParam String motivo) {
        ChatResponse response = chatService.escalarChat(chatId, motivo);
        return ResponseEntity.ok(response);
    }

    // WebSocket endpoints para tiempo real

    @MessageMapping("/chat/{chatId}/mensaje")
    @SendTo("/topic/chat/{chatId}")
    public MensajeResponse enviarMensajeWebSocket(
            @DestinationVariable UUID chatId,
            MensajeCreateRequest mensaje,
            Authentication authentication) {
        UUID remitenteId = obtenerUsuarioId(authentication);
        return chatService.enviarMensaje(chatId, remitenteId, mensaje);
    }

    @MessageMapping("/chat/{chatId}/typing")
    @SendTo("/topic/chat/{chatId}/typing")
    public String notificarEscribiendo(
            @DestinationVariable UUID chatId,
            String usuario) {
        return usuario + " está escribiendo...";
    }

    // Método utilitario
    private UUID obtenerUsuarioId(Authentication authentication) {
        // Implementar lógica para extraer UUID del usuario autenticado
        // Por ahora retornamos un UUID simulado
        return UUID.randomUUID();
    }
}