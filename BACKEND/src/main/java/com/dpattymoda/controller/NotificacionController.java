package com.dpattymoda.controller;

import com.dpattymoda.dto.request.NotificacionCreateRequest;
import com.dpattymoda.dto.response.NotificacionResponse;
import com.dpattymoda.service.NotificacionService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador para gestión de notificaciones
 */
@Tag(name = "Notificaciones", description = "Sistema de notificaciones")
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Operation(summary = "Enviar notificación", description = "Enviar notificación a usuario específico")
    @PostMapping("/enviar/{usuarioId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<NotificacionResponse> enviarNotificacion(
            @PathVariable UUID usuarioId,
            @Valid @RequestBody NotificacionCreateRequest request) {
        NotificacionResponse response = notificacionService.enviarNotificacion(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Notificación masiva", description = "Enviar notificación a todos los usuarios de un rol")
    @PostMapping("/masiva/{rol}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> enviarNotificacionMasiva(
            @PathVariable String rol,
            @Valid @RequestBody NotificacionCreateRequest request) {
        notificacionService.enviarNotificacionMasiva(rol, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Mis notificaciones", description = "Obtener notificaciones del usuario autenticado")
    @GetMapping("/mis-notificaciones")
    public ResponseEntity<Page<NotificacionResponse>> obtenerMisNotificaciones(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        Page<NotificacionResponse> response = notificacionService.obtenerNotificacionesUsuario(usuarioId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Marcar como leída", description = "Marcar notificación como leída")
    @PostMapping("/{notificacionId}/leer")
    public ResponseEntity<Void> marcarComoLeida(
            @PathVariable UUID notificacionId,
            Authentication authentication) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        notificacionService.marcarComoLeida(notificacionId, usuarioId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Marcar todas como leídas", description = "Marcar todas las notificaciones como leídas")
    @PostMapping("/marcar-todas-leidas")
    public ResponseEntity<Void> marcarTodasComoLeidas(Authentication authentication) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        notificacionService.marcarTodasComoLeidas(usuarioId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Contador no leídas", description = "Obtener cantidad de notificaciones no leídas")
    @GetMapping("/contador-no-leidas")
    public ResponseEntity<Integer> contarNoLeidas(Authentication authentication) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        Integer contador = notificacionService.contarNoLeidas(usuarioId);
        return ResponseEntity.ok(contador);
    }

    @Operation(summary = "Eliminar notificación", description = "Eliminar notificación específica")
    @DeleteMapping("/{notificacionId}")
    public ResponseEntity<Void> eliminarNotificacion(
            @PathVariable UUID notificacionId,
            Authentication authentication) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        notificacionService.eliminarNotificacion(notificacionId, usuarioId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Configurar preferencias", description = "Configurar preferencias de notificación")
    @PostMapping("/preferencias")
    public ResponseEntity<Void> configurarPreferencias(
            Authentication authentication,
            @Parameter(description = "Tipo de notificación") @RequestParam String tipoNotificacion,
            @Parameter(description = "Habilitado") @RequestParam boolean habilitado) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        notificacionService.configurarPreferencias(usuarioId, tipoNotificacion, habilitado);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Procesar cola", description = "Procesar notificaciones pendientes")
    @PostMapping("/procesar-cola")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> procesarColaPendientes() {
        notificacionService.procesarColaPendientes();
        return ResponseEntity.ok().build();
    }

    // Método utilitario
    private UUID obtenerUsuarioId(Authentication authentication) {
        // Implementar lógica para extraer UUID del usuario autenticado
        return UUID.randomUUID(); // Placeholder
    }
}