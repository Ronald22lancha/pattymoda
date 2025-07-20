package com.dpattymoda.controller;

import com.dpattymoda.dto.request.ResenaCreateRequest;
import com.dpattymoda.dto.request.ResenaUpdateRequest;
import com.dpattymoda.dto.response.ResenaResponse;
import com.dpattymoda.service.ResenaService;
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
 * Controlador para gestión de reseñas de productos
 */
@Tag(name = "Reseñas", description = "Sistema de calificaciones y reseñas")
@RestController
@RequestMapping("/api/reseñas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @Operation(summary = "Crear reseña", description = "Crear nueva reseña de producto")
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ResenaResponse> crearResena(
            Authentication authentication,
            @Valid @RequestBody ResenaCreateRequest request) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        ResenaResponse response = resenaService.crearResena(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar reseña", description = "Actualizar reseña existente")
    @PutMapping("/{resenaId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ResenaResponse> actualizarResena(
            @PathVariable UUID resenaId,
            Authentication authentication,
            @Valid @RequestBody ResenaUpdateRequest request) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        ResenaResponse response = resenaService.actualizarResena(resenaId, usuarioId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener reseña", description = "Consultar información de una reseña")
    @GetMapping("/{resenaId}")
    public ResponseEntity<ResenaResponse> obtenerResena(@PathVariable UUID resenaId) {
        ResenaResponse response = resenaService.obtenerResena(resenaId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reseñas por producto", description = "Listar reseñas de un producto")
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Page<ResenaResponse>> listarResenasPorProducto(
            @PathVariable UUID productoId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ResenaResponse> response = resenaService.listarResenasPorProducto(productoId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mis reseñas", description = "Listar reseñas del usuario autenticado")
    @GetMapping("/mis-reseñas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Page<ResenaResponse>> listarMisResenas(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        Page<ResenaResponse> response = resenaService.listarResenasUsuario(usuarioId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reseñas pendientes", description = "Listar reseñas pendientes de moderación")
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<ResenaResponse>> listarResenasPendientes(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ResenaResponse> response = resenaService.listarResenasPendientes(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Moderar reseña", description = "Aprobar o rechazar reseña")
    @PostMapping("/{resenaId}/moderar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ResenaResponse> moderarResena(
            @PathVariable UUID resenaId,
            @Parameter(description = "Decisión de moderación") @RequestParam String decision,
            @Parameter(description = "Motivo si es rechazo") @RequestParam(required = false) String motivo) {
        ResenaResponse response = resenaService.moderarResena(resenaId, decision, motivo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Marcar como útil", description = "Marcar reseña como útil o no útil")
    @PostMapping("/{resenaId}/utilidad")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> marcarComoUtil(
            @PathVariable UUID resenaId,
            Authentication authentication,
            @Parameter(description = "Es útil") @RequestParam boolean util) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        resenaService.marcarComoUtil(resenaId, usuarioId, util);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reportar reseña", description = "Reportar reseña como inapropiada")
    @PostMapping("/{resenaId}/reportar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> reportarResena(
            @PathVariable UUID resenaId,
            Authentication authentication,
            @Parameter(description = "Motivo del reporte") @RequestParam String motivo) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        resenaService.reportarResena(resenaId, usuarioId, motivo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Eliminar reseña", description = "Eliminar reseña propia")
    @DeleteMapping("/{resenaId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> eliminarResena(
            @PathVariable UUID resenaId,
            Authentication authentication) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        resenaService.eliminarResena(resenaId, usuarioId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Estadísticas de producto", description = "Obtener estadísticas de reseñas por producto")
    @GetMapping("/estadisticas/{productoId}")
    public ResponseEntity<ResenaService.EstadisticasResenasResponse> obtenerEstadisticasProducto(
            @PathVariable UUID productoId) {
        ResenaService.EstadisticasResenasResponse response = resenaService.obtenerEstadisticasProducto(productoId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verificar elegibilidad", description = "Verificar si usuario puede reseñar producto")
    @GetMapping("/puede-resenar/{productoId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Boolean> puedeResenarProducto(
            @PathVariable UUID productoId,
            Authentication authentication) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        boolean puedeResenar = resenaService.puedeResenarProducto(usuarioId, productoId);
        return ResponseEntity.ok(puedeResenar);
    }

    // Método utilitario
    private UUID obtenerUsuarioId(Authentication authentication) {
        return UUID.randomUUID(); // Placeholder
    }
}