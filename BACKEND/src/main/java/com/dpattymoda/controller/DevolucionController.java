package com.dpattymoda.controller;

import com.dpattymoda.dto.request.DevolucionCreateRequest;
import com.dpattymoda.dto.request.DevolucionUpdateRequest;
import com.dpattymoda.dto.response.DevolucionResponse;
import com.dpattymoda.service.DevolucionService;
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
 * Controlador para gestión de devoluciones
 */
@Tag(name = "Devoluciones", description = "Gestión de devoluciones y cambios")
@RestController
@RequestMapping("/api/devoluciones")
@RequiredArgsConstructor
public class DevolucionController {

    private final DevolucionService devolucionService;

    @Operation(summary = "Solicitar devolución", description = "Crear nueva solicitud de devolución")
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<DevolucionResponse> solicitarDevolucion(
            Authentication authentication,
            @Valid @RequestBody DevolucionCreateRequest request) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        DevolucionResponse response = devolucionService.solicitarDevolucion(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener devolución", description = "Consultar información de una devolución")
    @GetMapping("/{devolucionId}")
    public ResponseEntity<DevolucionResponse> obtenerDevolucion(@PathVariable UUID devolucionId) {
        DevolucionResponse response = devolucionService.obtenerDevolucion(devolucionId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mis devoluciones", description = "Listar devoluciones del usuario")
    @GetMapping("/mis-devoluciones")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Page<DevolucionResponse>> listarMisDevoluciones(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        Page<DevolucionResponse> response = devolucionService.listarDevolucionesUsuario(usuarioId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Todas las devoluciones", description = "Listar todas las devoluciones (admin/empleado)")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<DevolucionResponse>> listarTodasDevoluciones(
            @Parameter(description = "Estado de la devolución") @RequestParam(required = false) String estado,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<DevolucionResponse> response = devolucionService.listarTodasDevoluciones(estado, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Aprobar devolución", description = "Aprobar solicitud de devolución")
    @PostMapping("/{devolucionId}/aprobar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<DevolucionResponse> aprobarDevolucion(
            @PathVariable UUID devolucionId,
            Authentication authentication,
            @Parameter(description = "Observaciones") @RequestParam(required = false) String observaciones) {
        UUID empleadoId = obtenerUsuarioId(authentication);
        DevolucionResponse response = devolucionService.aprobarDevolucion(devolucionId, empleadoId, observaciones);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Rechazar devolución", description = "Rechazar solicitud de devolución")
    @PostMapping("/{devolucionId}/rechazar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<DevolucionResponse> rechazarDevolucion(
            @PathVariable UUID devolucionId,
            Authentication authentication,
            @Parameter(description = "Motivo del rechazo") @RequestParam String motivo) {
        UUID empleadoId = obtenerUsuarioId(authentication);
        DevolucionResponse response = devolucionService.rechazarDevolucion(devolucionId, empleadoId, motivo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Procesar devolución", description = "Procesar recepción de items devueltos")
    @PostMapping("/{devolucionId}/procesar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<DevolucionResponse> procesarDevolucion(
            @PathVariable UUID devolucionId,
            @Valid @RequestBody DevolucionUpdateRequest request) {
        DevolucionResponse response = devolucionService.procesarDevolucion(devolucionId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Completar devolución", description = "Finalizar proceso de devolución")
    @PostMapping("/{devolucionId}/completar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<DevolucionResponse> completarDevolucion(
            @PathVariable UUID devolucionId,
            @Parameter(description = "Método de reembolso") @RequestParam String metodoReembolso) {
        DevolucionResponse response = devolucionService.completarDevolucion(devolucionId, metodoReembolso);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cancelar devolución", description = "Cancelar solicitud de devolución")
    @PostMapping("/{devolucionId}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cancelarDevolucion(
            @PathVariable UUID devolucionId,
            Authentication authentication,
            @Parameter(description = "Motivo de cancelación") @RequestParam String motivo) {
        UUID usuarioId = obtenerUsuarioId(authentication);
        devolucionService.cancelarDevolucion(devolucionId, usuarioId, motivo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Estadísticas", description = "Obtener estadísticas de devoluciones")
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<DevolucionService.EstadisticasDevolucionResponse> obtenerEstadisticas() {
        DevolucionService.EstadisticasDevolucionResponse response = devolucionService.obtenerEstadisticas();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Generar etiqueta", description = "Generar etiqueta de envío para devolución")
    @PostMapping("/{devolucionId}/etiqueta-envio")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<String> generarEtiquetaEnvio(@PathVariable UUID devolucionId) {
        String urlEtiqueta = devolucionService.generarEtiquetaEnvio(devolucionId);
        return ResponseEntity.ok(urlEtiqueta);
    }

    @Operation(summary = "Validar elegibilidad", description = "Validar si un pedido es elegible para devolución")
    @GetMapping("/validar-elegibilidad/{pedidoId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Boolean> validarElegibilidad(
            @PathVariable UUID pedidoId,
            @Parameter(description = "Items a devolver") @RequestParam java.util.List<UUID> itemsDevolucion) {
        boolean elegible = devolucionService.validarElegibilidadDevolucion(pedidoId, itemsDevolucion);
        return ResponseEntity.ok(elegible);
    }

    // Método utilitario
    private UUID obtenerUsuarioId(Authentication authentication) {
        return UUID.randomUUID(); // Placeholder
    }
}