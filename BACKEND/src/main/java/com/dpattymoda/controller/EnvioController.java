package com.dpattymoda.controller;

import com.dpattymoda.dto.request.EnvioCreateRequest;
import com.dpattymoda.dto.request.ActualizarEnvioRequest;
import com.dpattymoda.dto.response.EnvioResponse;
import com.dpattymoda.dto.response.SeguimientoEnvioResponse;
import com.dpattymoda.service.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controlador para gestión de envíos
 */
@Tag(name = "Envíos", description = "Gestión de envíos y seguimiento")
@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @Operation(summary = "Crear envío", description = "Crear envío para un pedido")
    @PostMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<EnvioResponse> crearEnvio(
            @PathVariable UUID pedidoId,
            @Valid @RequestBody EnvioCreateRequest request) {
        EnvioResponse response = envioService.crearEnvio(pedidoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener envío", description = "Consultar información de un envío")
    @GetMapping("/{envioId}")
    public ResponseEntity<EnvioResponse> obtenerEnvio(@PathVariable UUID envioId) {
        EnvioResponse response = envioService.obtenerEnvio(envioId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar por guía", description = "Buscar envío por número de guía")
    @GetMapping("/guia/{numeroGuia}")
    public ResponseEntity<EnvioResponse> obtenerEnvioPorGuia(@PathVariable String numeroGuia) {
        EnvioResponse response = envioService.obtenerEnvioPorGuia(numeroGuia);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar estado", description = "Actualizar estado del envío")
    @PutMapping("/{envioId}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<EnvioResponse> actualizarEstado(
            @PathVariable UUID envioId,
            @Valid @RequestBody ActualizarEnvioRequest request) {
        EnvioResponse response = envioService.actualizarEstadoEnvio(envioId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Seguimiento público", description = "Obtener seguimiento detallado (público)")
    @GetMapping("/seguimiento/{numeroGuia}")
    public ResponseEntity<SeguimientoEnvioResponse> obtenerSeguimiento(@PathVariable String numeroGuia) {
        SeguimientoEnvioResponse response = envioService.obtenerSeguimiento(numeroGuia);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Marcar entregado", description = "Marcar envío como entregado")
    @PostMapping("/{envioId}/entregar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<EnvioResponse> marcarComoEntregado(
            @PathVariable UUID envioId,
            @Parameter(description = "Evidencia de entrega") @RequestParam String evidenciaEntrega) {
        EnvioResponse response = envioService.marcarComoEntregado(envioId, evidenciaEntrega);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Intento fallido", description = "Registrar intento de entrega fallido")
    @PostMapping("/{envioId}/intento-fallido")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<EnvioResponse> registrarIntentoFallido(
            @PathVariable UUID envioId,
            @Parameter(description = "Motivo del fallo") @RequestParam String motivo) {
        EnvioResponse response = envioService.registrarIntentoFallido(envioId, motivo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Calcular costo", description = "Calcular costo de envío")
    @GetMapping("/calcular-costo")
    public ResponseEntity<BigDecimal> calcularCostoEnvio(
            @Parameter(description = "Ciudad destino") @RequestParam String ciudadDestino,
            @Parameter(description = "Departamento destino") @RequestParam String departamentoDestino,
            @Parameter(description = "Peso total") @RequestParam BigDecimal pesoTotal) {
        BigDecimal costo = envioService.calcularCostoEnvio(ciudadDestino, departamentoDestino, pesoTotal);
        return ResponseEntity.ok(costo);
    }

    @Operation(summary = "Transportistas disponibles", description = "Obtener transportistas para una ciudad")
    @GetMapping("/transportistas")
    public ResponseEntity<List<EnvioService.TransportistaResponse>> obtenerTransportistas(
            @Parameter(description = "Ciudad destino") @RequestParam String ciudadDestino) {
        List<EnvioService.TransportistaResponse> response = envioService.obtenerTransportistasDisponibles(ciudadDestino);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Generar etiqueta", description = "Generar etiqueta de envío")
    @PostMapping("/{envioId}/etiqueta")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<String> generarEtiqueta(@PathVariable UUID envioId) {
        String urlEtiqueta = envioService.generarEtiquetaEnvio(envioId);
        return ResponseEntity.ok(urlEtiqueta);
    }

    @Operation(summary = "Sincronizar transportista", description = "Sincronizar estado con API del transportista")
    @PostMapping("/{envioId}/sincronizar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Void> sincronizarConTransportista(@PathVariable UUID envioId) {
        envioService.sincronizarConTransportista(envioId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Envíos pendientes", description = "Obtener lista de envíos pendientes")
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<List<EnvioResponse>> obtenerEnviosPendientes() {
        List<EnvioResponse> response = envioService.obtenerEnviosPendientes();
        return ResponseEntity.ok(response);
    }
}